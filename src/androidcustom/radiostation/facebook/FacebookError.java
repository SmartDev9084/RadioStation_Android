/*
 * Copyright 2010 Facebook, Inc.
 *
 * Licensed under the Apache License, Version 2.0 (the "License");
 * you may not use this file except in compliance with the License.
 * You may obtain a copy of the License at
 *
 *	http://www.apache.org/licenses/LICENSE-2.0
 *
 * Unless required by applicable law or agreed to in writing, software
 * distributed under the License is distributed on an "AS IS" BASIS,
 * WITHOUT WARRANTIES OR CONDITIONS OF ANY KIND, either express or implied.
 * See the License for the specific language governing permissions and
 * limitations under the License.
 */

package androidcustom.radiostation.facebook;

/**
 * Encapsulation of a Facebook Error: a Facebook request that could not be
 * fulfilled.
 *
 * @author ssoneff@facebook.com
 */
//==============================================================================
public class FacebookError extends RuntimeException {

	private static final long serialVersionUID = 1L;

	private int		m_iErrorCode = 0;
	private String	m_strErrorType;

	//------------------------------------------------------------------------------
	public FacebookError(String message) {
		super(message);
	}

	//------------------------------------------------------------------------------
	public FacebookError(String message, String type, int code) {
		super(message);
		m_strErrorType = type;
		m_iErrorCode = code;
	}

	//------------------------------------------------------------------------------
	public int GetErrorCode() {
		return m_iErrorCode;
	}

	//------------------------------------------------------------------------------
	public String GetErrorType() {
		return m_strErrorType;
	}

}

//==============================================================================
