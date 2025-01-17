/*
 * Copyright 2017 - Alain CHARLES
 *
 * Licensed under the Apache License, Version 2.0 (the "License");
 * you may not use this file except in compliance with the License.
 * You may obtain a copy of the License at
 *
 *         http://www.apache.org/licenses/LICENSE-2.0
 *
 * Unless required by applicable law or agreed to in writing, software
 * distributed under the License is distributed on an "AS IS" BASIS,
 * WITHOUT WARRANTIES OR CONDITIONS OF ANY KIND, either express or implied.
 * See the License for the specific language governing permissions and
 * limitations under the License
 */

package fr.cerema.dsi.commons.beans.errors;

import java.time.LocalDateTime;
import java.util.ArrayList;
import java.util.List;
import java.util.Objects;

import org.springframework.http.HttpStatus;

import com.fasterxml.jackson.annotation.JsonFormat;
import com.fasterxml.jackson.annotation.JsonIgnore;
import com.fasterxml.jackson.annotation.JsonProperty;
import com.fasterxml.jackson.annotation.JsonPropertyOrder;
import com.fasterxml.jackson.dataformat.xml.annotation.JacksonXmlElementWrapper;
import com.fasterxml.jackson.dataformat.xml.annotation.JacksonXmlProperty;

/**
 * This class holds the necessary fields of the error
 * that has to be sent in case of API call failure.
 * <p>
 * Error fields are stored in an instance of an inner class {@link JsonApiError.ApiError}.
 * The Json error is sent back as an array (one element in this case) as asked by the Json Api Specification V1.0
 * See <a href="http://jsonapi.org/format/#errors">http://jsonapi.org/format/#errors</a>
 * <p>
 * Generally, your {@link org.springframework.web.bind.annotation.RestController} will
 * throw an exception and you will instantiate {@link JsonApiError} in a exception handler
 * as done for instance in {@link fr.cerema.dsi.commons.controllers.RestResponseEntityExceptionHandler}
 *
 * @author alain.charles
 */
public class JsonApiError {

  private JsonApiError.ApiError error;

  /**
   * Returns the API Error as an array of one error in this case
   * as aked by Json Api Specification V1.0
   *
   * @return the list of (one) error.
   */
  @JsonProperty
  @JacksonXmlElementWrapper(localName = "errors")
  @JacksonXmlProperty(localName = "error")
  public List<JsonApiError.ApiError> getErrors() {
    List<ApiError> returnValue = new ArrayList<>();
    returnValue.add(this.error);
    return returnValue;
  }

  private JsonApiError() {
    this.error = new JsonApiError.ApiError();
  }

  /**
   * Creates a new instance of {@link JsonApiError}
   * using supplied {@link HttpStatus}
   *
   * @param status the HTTP status
   */
  public JsonApiError(HttpStatus status) {
    this();
    this.error.status = status;
    this.error.message = status.getReasonPhrase();
  }

  /**
   * Creates a new instance of {@link JsonApiError}
   * using supplied {@link HttpStatus} and {@link Throwable}
   *
   * @param status the HTTP status
   * @param ex     the Throwable whose message will be included in {@link JsonApiError#getDebugMessage()}
   */
  public JsonApiError(HttpStatus status, Throwable ex) {
    this(status);
    if (Objects.nonNull(ex)) this.error.debugMessage = ex.getLocalizedMessage();
  }

  /**
   * Creates a new instance of {@link JsonApiError}
   * using supplied {@link HttpStatus}, {@link String} and {@link Throwable}
   *
   * @param status  the HTTP status
   * @param message the message that will be stored in {@link JsonApiError#getMessage()}
   * @param ex      the Throwable whose message will be included in {@link JsonApiError#getDebugMessage()}
   */
  public JsonApiError(HttpStatus status, String message, Throwable ex) {
    this(status, ex);
    this.error.message = message;
  }


  /**
   * Returns the error time stamp
   *
   * @return the timestamp
   */
  @JsonIgnore
  public LocalDateTime getTimestamp() {
    return error.timestamp;
  }

  /**
   * Returns the Http Status Code value
   *
   * @return the status code value
   */
  @JsonIgnore
  public int getHttpErrorCode() {
    return error.status.value();
  }

  /**
   * Returns the reason of Http Error
   *
   * <p>See <a href="https://www.w3.org/Protocols/rfc2616/rfc2616-sec6.html">
   * https://www.w3.org/Protocols/rfc2616/rfc2616-sec6.html</a>
   *
   * @return the reason of error
   */
  @JsonIgnore
  public String getStatusReasonPhrase() {
    return error.status.getReasonPhrase();
  }

  /**
   * Returns the error main message
   *
   * @return the main message
   */
  @JsonIgnore
  public String getMessage() {
    return error.message;
  }

  /**
   * Returns the error debug message
   *
   * @return the debug message
   */
  @JsonIgnore
  private String getDebugMessage() {
    return error.debugMessage;
  }

  /**
   * Returns the list of {@link ApiSubError} instances or {@code null}
   *
   * @return the list of subErrors
   */
  @JsonIgnore
  public List<ApiSubError> getSubErrors() {
    return error.subErrors;
  }

  /**
   * Retourne the {@link HttpStatus} of the error
   *
   * @return the Http status
   */
  @JsonIgnore
  public HttpStatus getStatus() {
    return error.status;
  }


  @JsonIgnore
  public List<ApiSubError> getApiSubErrors() {
    return error.getSubErrors();
  }

  /**
   * Minimalist output of the error contained in this instance
   * Used when the json or xml serialization failed
   */
  @Override
  public String toString() {
    return error.toString();
  }

  /**
   * This class holds the necessary fields of the error
   * that has to be sent in case of API call failure.
   *
   * <p>Generally, you will instantiate {@link JsonApiError} in the
   * {@link org.springframework.web.bind.annotation.RestController} if the API call raised an exception.
   *
   * @author alain.charles
   */
  @JsonPropertyOrder({"httpErrorCode", "reason", "timestamp", "message", "debugMessage", "subErrors"})
  public class ApiError {

    private HttpStatus status;

    private LocalDateTime timestamp;

    private String message;

    private String debugMessage;

    private List<ApiSubError> subErrors;

    private ApiError() {
      timestamp = LocalDateTime.now();
    }


    /**
     * Returns the error time stamp
     *
     * @return the timestamp
     */
    @JsonProperty
    @JsonFormat(shape = JsonFormat.Shape.STRING, pattern = "dd-MM-yyyy HH:mm:ss")
    public LocalDateTime getTimestamp() {
      return timestamp;
    }

    /**
     * Returns the Http Status Code value
     *
     * @return the status code value
     */
    @JsonProperty
    public int getHttpErrorCode() {
      return status.value();
    }

    /**
     * Returns the reason of Http Error
     *
     * <p>See <a href="https://www.w3.org/Protocols/rfc2616/rfc2616-sec6.html">
     * https://www.w3.org/Protocols/rfc2616/rfc2616-sec6.html</a>
     *
     * @return the reason of error
     */
    @JsonProperty("reason")
    public String getStatusReasonPhrase() {
      return status.getReasonPhrase();
    }

    /**
     * Returns the error main message
     *
     * @return the main message
     */
    @JsonProperty
    public String getMessage() {
      return message;
    }

    /**
     * Returns the error debug message
     *
     * @return the debug message
     */
    @JsonProperty
    public String getDebugMessage() {
      return debugMessage;
    }

    /**
     * Returns the list of {@link ApiSubError} instances or {@code null}
     *
     * @return the list of subErrors
     */
    @JsonProperty
    List<ApiSubError> getSubErrors() {
      return subErrors;
    }

    @Override
    public String toString() {
      final String UNKNOWN = "inconnu";

      StringBuilder sb = new StringBuilder();

      sb.append("httpErrorCode : ");
      sb.append(error.status != null ? error.status.value() : UNKNOWN);
      sb.append("\nreason : ");
      sb.append(error.status != null ? error.status.getReasonPhrase() : UNKNOWN);
      sb.append("\ntimestamp : ");
      sb.append(error.timestamp != null ? error.timestamp : UNKNOWN);
      sb.append("\nmessage : ");
      sb.append(error.message != null ? error.message : UNKNOWN);
      sb.append("\ndebugMessage : ");
      sb.append(error.debugMessage != null ? error.debugMessage : UNKNOWN);
      sb.append("\nsubErrors : ");
      sb.append(error.subErrors != null ? error.subErrors : UNKNOWN);

      return sb.toString();
    }
  }
}
