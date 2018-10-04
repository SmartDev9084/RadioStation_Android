package androidcustom.radiostation.chat.ui;

import java.util.ArrayList;
import java.util.Date;
import java.util.List;

import android.content.Context;
import android.os.Bundle;
import android.os.Handler;
import android.text.InputType;
import android.text.format.DateUtils;
import android.view.MenuItem;
import android.view.View;
import android.view.ViewGroup;
import android.view.inputmethod.InputMethodManager;
import android.widget.AbsListView;
import android.widget.BaseAdapter;
import android.widget.EditText;
import android.widget.ListView;
import android.widget.TextView;
import androidcustom.radiostation.R;
import androidcustom.radiostation.chat.model.Conversation;
import androidcustom.radiostation.global.Const;
import androidcustom.radiostation.loader.LoaderImage;

import com.parse.FindCallback;
import com.parse.ParseException;
import com.parse.ParseObject;
import com.parse.ParseQuery;
import com.parse.SaveCallback;

 //=============================================================================
/**
 * The Class Chat is the Activity class that holds main chat screen. It shows
 * all the conversation messages between two users and also allows the user to
 * send and receive messages.
 */
public class ActivityChat extends ActivityCustom
{

	private	ArrayList<Conversation> convList;	// The Conversation list
	private	ChatAdapter		m_adapterChat;		// The chat adapter
	private	EditText		m_editText;			// The Editext to compose the message
	private	String			m_strBuddy;			// The user name of buddy
	private	Date			m_dateLastMsg;		// The date of last message in conversation
	private	boolean			m_bIsRunning;		// Flag to hold if the activity is running or not
	private	static Handler	m_handler;			// The handler

	//------------------------------------------------------------------------------
	@Override
	protected void onCreate(Bundle savedInstanceState) {
		super.onCreate(savedInstanceState);
		setContentView(R.layout.activity_chat);

		if (LoaderImage.GetInstance() != null)
			LoaderImage.GetInstance().ClearCache();

		convList = new ArrayList<Conversation>();
		ListView list = (ListView)findViewById(R.id.list);
		m_adapterChat = new ChatAdapter();
		list.setAdapter(m_adapterChat);
		list.setTranscriptMode(AbsListView.TRANSCRIPT_MODE_ALWAYS_SCROLL);
		list.setStackFromBottom(true);

		m_editText = (EditText) findViewById(R.id.txt);
		m_editText.setInputType(InputType.TYPE_CLASS_TEXT | InputType.TYPE_TEXT_FLAG_MULTI_LINE);

		setTouchNClick(R.id.btnSend);

		m_strBuddy = getIntent().getStringExtra(Const.EXTRA_DATA);
		getActionBar().setTitle(m_strBuddy);

		m_handler = new Handler();
	}

	//------------------------------------------------------------------------------
	@Override
	protected void onResume() {
		super.onResume();
		m_bIsRunning = true;
		LoadConversationList();
	}

	//------------------------------------------------------------------------------
	@Override
	protected void onDestroy() {
		if (LoaderImage.GetInstance() != null)
			LoaderImage.GetInstance().ClearCache();
		super.onDestroy();
	}

	//------------------------------------------------------------------------------
	@Override
	protected void onPause() {
		super.onPause();
		m_bIsRunning = false;
	}

	//------------------------------------------------------------------------------
	@Override
	public void onClick(View v) {
		super.onClick(v);
		if (v.getId() == R.id.btnSend) {
			SendMessage();
		}
	}

	//------------------------------------------------------------------------------
	/**
	 * Call this method to Send message to opponent. It does nothing if the text
	 * is empty otherwise it creates a Parse object for Chat message and send it
	 * to Parse server.
	 */
	private void SendMessage()
	{
		if (m_editText.length() == 0)
			return;

		InputMethodManager imm = (InputMethodManager) getSystemService(Context.INPUT_METHOD_SERVICE);
		imm.hideSoftInputFromWindow(m_editText.getWindowToken(), 0);

		String s = m_editText.getText().toString();
		final Conversation conv = new Conversation(s, new Date(),
				ActivityChatUserList.user.getUsername());
		conv.setStatus(Conversation.STATUS_SENDING);
		convList.add(conv);
		m_adapterChat.notifyDataSetChanged();
		m_editText.setText(null);

		ParseObject po = new ParseObject("Chat");
		po.put("sender", ActivityChatUserList.user.getUsername());
		po.put("receiver", m_strBuddy);
		// po.put("createdAt", "");
		po.put("message", s);
		po.saveEventually(
			new SaveCallback() {
				@Override
				public void done(ParseException e)
				{
					if (e == null)
						conv.setStatus(Conversation.STATUS_SENT);
					else
						conv.setStatus(Conversation.STATUS_FAILED);
					m_adapterChat.notifyDataSetChanged();
				}
			}
		);
	}

	//------------------------------------------------------------------------------
	/**
	 * Load the conversation list from Parse server and save the date of last
	 * message that will be used to load only recent new messages
	 */
	private void LoadConversationList() {
		ParseQuery<ParseObject> q = ParseQuery.getQuery("Chat");
		if (convList.size() == 0) {
			// load all messages...
			ArrayList<String> al = new ArrayList<String>();
			al.add(m_strBuddy);
			al.add(ActivityChatUserList.user.getUsername());
			q.whereContainedIn("sender", al);
			q.whereContainedIn("receiver", al);
		}
		else {
			// load only newly received message..
			if (m_dateLastMsg != null)
				q.whereGreaterThan("createdAt", m_dateLastMsg);
			q.whereEqualTo("sender", m_strBuddy);
			q.whereEqualTo("receiver", ActivityChatUserList.user.getUsername());
		}
		q.orderByDescending("createdAt");
		q.setLimit(30);
		q.findInBackground(
			new FindCallback<ParseObject>() {
				@Override
				public void done(List<ParseObject> li, ParseException e) {
					if (li != null && li.size() > 0) {
						for (int i = li.size() - 1; i >= 0; i--) {
							ParseObject po = li.get(i);
							Conversation c = new Conversation(
								po.getString("message"),
								po.getCreatedAt(),
								po.getString("sender")
							);
							convList.add(c);
							if (m_dateLastMsg == null
									|| m_dateLastMsg.before(c.getDate()))
								m_dateLastMsg = c.getDate();
							m_adapterChat.notifyDataSetChanged();
						}
					}
					m_handler.postDelayed(
						new Runnable() {
							@Override
							public void run() {
								if (m_bIsRunning)
									LoadConversationList();
							}
						},
						1000
					);
				}
			}
		);
	}

	//==============================================================================
	private class ChatAdapter extends BaseAdapter
	{
		//------------------------------------------------------------------------------
		@Override
		public int getCount() {
			return convList.size();
		}

		//------------------------------------------------------------------------------
		@Override
		public Conversation getItem(int arg0) {
			return convList.get(arg0);
		}

		//------------------------------------------------------------------------------
		@Override
		public long getItemId(int arg0) {
			return arg0;
		}

		//------------------------------------------------------------------------------
		@Override
		public View getView(int position, View convertView, ViewGroup parent)
		{
			Conversation conv = getItem(position);
			if (conv.isSent())
				convertView = getLayoutInflater().inflate(R.layout.item_chat_sent, null);
			else
				convertView = getLayoutInflater().inflate(R.layout.item_chat_rcv, null);

			TextView textView = (TextView) convertView.findViewById(R.id.lbl1);
			textView.setText(DateUtils.getRelativeDateTimeString(ActivityChat.this, conv
					.getDate().getTime(), DateUtils.SECOND_IN_MILLIS,
					DateUtils.DAY_IN_MILLIS, 0));

			textView = (TextView) convertView.findViewById(R.id.lbl2);
			textView.setText(conv.getMsg());

			textView = (TextView) convertView.findViewById(R.id.lbl3);
			if (conv.isSent()) {
				if (conv.getStatus() == Conversation.STATUS_SENT)
					textView.setText("Delivered");
				else if (conv.getStatus() == Conversation.STATUS_SENDING)
					textView.setText("Sending...");
				else
					textView.setText("Failed");
			}
			else
				textView.setText("");
			return convertView;
		}
	}
	//==============================================================================

	//------------------------------------------------------------------------------
	/* (non-Javadoc)
	 * @see android.app.Activity#onOptionsItemSelected(android.view.MenuItem)
	 */
	@Override
	public boolean onOptionsItemSelected(MenuItem item) {
		if (item.getItemId() == android.R.id.home) {
			finish();
		}
		return super.onOptionsItemSelected(item);
	}

	//------------------------------------------------------------------------------

}
 //=============================================================================
