/*
 * Copyright (c) 2018 - Alain CHARLES
 *
 *  Licensed under the CeCILL Version 2.0 License (the "License");
 *  you may not use this file except in compliance with the License.
 *  You may obtain a copy of the License at
 *
 *           http://www.cecill.info/licences/Licence_CeCILL_V2-fr.html
 *
 *  Unless required by applicable law or agreed to in writing, software
 *  distributed under the License is distributed on an "AS IS" BASIS,
 *  WITHOUT WARRANTIES OR CONDITIONS OF ANY KIND, either express or implied.
 *  See the License for the specific language governing permissions and
 *  limitations under the License
 */

package fr.cerema.dsi.commons.exceptions;


/**
 * Exception levé quand le WS ne renvoie pas de résultat
 * <p>
 * Only extending exceptions are throw by {@link BadRequestException}
 */
public class BadRequestException extends RuntimeException {

  /**
	 * 
	 */
	private static final long serialVersionUID = -479857823004400769L;

/**
   * Constructor
   *
   * @param message the message
   */
  public BadRequestException(String message) {
    super(message);
  }

  /**
   * Constructor
   *
   * @param message the message
   * @param cause   the cause
   */
  public BadRequestException(String message, Throwable cause) {
    super(message, cause);
  }
}
