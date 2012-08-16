/* 
 * Copyright 2011 Vaadin Ltd.
 * 
 * Licensed under the Apache License, Version 2.0 (the "License"); you may not
 * use this file except in compliance with the License. You may obtain a copy of
 * the License at
 * 
 * http://www.apache.org/licenses/LICENSE-2.0
 * 
 * Unless required by applicable law or agreed to in writing, software
 * distributed under the License is distributed on an "AS IS" BASIS, WITHOUT
 * WARRANTIES OR CONDITIONS OF ANY KIND, either express or implied. See the
 * License for the specific language governing permissions and limitations under
 * the License.
 */

package com.vaadin.data;

import java.io.Serializable;

import com.vaadin.data.Validator.InvalidValueException;

/**
 * <p>
 * Defines the interface to commit and discard changes to an object, supporting
 * read-through and write-through modes.
 * </p>
 * 
 * <p>
 * <i>Read-through mode</i> means that the value read from the buffered object
 * is constantly up to date with the data source. <i>Write-through</i> mode
 * means that all changes to the object are immediately updated to the data
 * source.
 * </p>
 * 
 * <p>
 * Since these modes are independent, their combinations may result in some
 * behaviour that may sound surprising.
 * </p>
 * 
 * <p>
 * For example, if a <code>Buffered</code> object is in read-through mode but
 * not in write-through mode, the result is an object whose value is updated
 * directly from the data source only if it's not locally modified. If the value
 * is locally modified, retrieving the value from the object would result in a
 * value that is different than the one stored in the data source, even though
 * the object is in read-through mode.
 * </p>
 * 
 * @author Vaadin Ltd.
 * @since 3.0
 */
public interface Buffered extends Serializable {

    /**
     * Updates all changes since the previous commit to the data source. The
     * value stored in the object will always be updated into the data source
     * when <code>commit</code> is called.
     * 
     * @throws SourceException
     *             if the operation fails because of an exception is thrown by
     *             the data source. The cause is included in the exception.
     * @throws InvalidValueException
     *             if the operation fails because validation is enabled and the
     *             values do not validate
     */
    public void commit() throws SourceException, InvalidValueException;

    /**
     * Discards all changes since last commit. The object updates its value from
     * the data source.
     * 
     * @throws SourceException
     *             if the operation fails because of an exception is thrown by
     *             the data source. The cause is included in the exception.
     */
    public void discard() throws SourceException;

    /**
     * Tests if the object is in write-through mode. If the object is in
     * write-through mode, all modifications to it will result in
     * <code>commit</code> being called after the modification.
     * 
     * @return <code>true</code> if the object is in write-through mode,
     *         <code>false</code> if it's not.
     * @deprecated Use {@link #setBuffered(boolean)} instead. Note that
     *             setReadThrough(true), setWriteThrough(true) equals
     *             setBuffered(false)
     */
    @Deprecated
    public boolean isWriteThrough();

    /**
     * Sets the object's write-through mode to the specified status. When
     * switching the write-through mode on, the <code>commit</code> operation
     * will be performed.
     * 
     * @param writeThrough
     *            Boolean value to indicate if the object should be in
     *            write-through mode after the call.
     * @throws SourceException
     *             If the operation fails because of an exception is thrown by
     *             the data source.
     * @throws InvalidValueException
     *             If the implicit commit operation fails because of a
     *             validation error.
     * 
     * @deprecated Use {@link #setBuffered(boolean)} instead. Note that
     *             setReadThrough(true), setWriteThrough(true) equals
     *             setBuffered(false)
     */
    @Deprecated
    public void setWriteThrough(boolean writeThrough) throws SourceException,
            InvalidValueException;

    /**
     * Tests if the object is in read-through mode. If the object is in
     * read-through mode, retrieving its value will result in the value being
     * first updated from the data source to the object.
     * <p>
     * The only exception to this rule is that when the object is not in
     * write-through mode and it's buffer contains a modified value, the value
     * retrieved from the object will be the locally modified value in the
     * buffer which may differ from the value in the data source.
     * </p>
     * 
     * @return <code>true</code> if the object is in read-through mode,
     *         <code>false</code> if it's not.
     * @deprecated Use {@link #isBuffered(boolean)} instead. Note that
     *             setReadThrough(true), setWriteThrough(true) equals
     *             setBuffered(false)
     */
    @Deprecated
    public boolean isReadThrough();

    /**
     * Sets the object's read-through mode to the specified status. When
     * switching read-through mode on, the object's value is updated from the
     * data source.
     * 
     * @param readThrough
     *            Boolean value to indicate if the object should be in
     *            read-through mode after the call.
     * 
     * @throws SourceException
     *             If the operation fails because of an exception is thrown by
     *             the data source. The cause is included in the exception.
     * @deprecated Use {@link #setBuffered(boolean)} instead. Note that
     *             setReadThrough(true), setWriteThrough(true) equals
     *             setBuffered(false)
     */
    @Deprecated
    public void setReadThrough(boolean readThrough) throws SourceException;

    /**
     * Sets the object's buffered mode to the specified status.
     * <p>
     * When the object is in buffered mode, an internal buffer will be used to
     * store changes until {@link #commit()} is called. Calling
     * {@link #discard()} will revert the internal buffer to the value of the
     * data source.
     * </p>
     * <p>
     * This is an easier way to use {@link #setReadThrough(boolean)} and
     * {@link #setWriteThrough(boolean)} and not as error prone. Changing
     * buffered mode will change both the read through and write through state
     * of the object.
     * </p>
     * <p>
     * Mixing calls to {@link #setBuffered(boolean)}/{@link #isBuffered()} and
     * {@link #setReadThrough(boolean)}/{@link #isReadThrough()} or
     * {@link #setWriteThrough(boolean)}/{@link #isWriteThrough()} is generally
     * a bad idea.
     * </p>
     * 
     * @param buffered
     *            true if buffered mode should be turned on, false otherwise
     * @since 7.0
     */
    public void setBuffered(boolean buffered);

    /**
     * Checks the buffered mode of this Object.
     * <p>
     * This method only returns true if both read and write buffering is used.
     * </p>
     * 
     * @return true if buffered mode is on, false otherwise
     * @since 7.0
     */
    public boolean isBuffered();

    /**
     * Tests if the value stored in the object has been modified since it was
     * last updated from the data source.
     * 
     * @return <code>true</code> if the value in the object has been modified
     *         since the last data source update, <code>false</code> if not.
     */
    public boolean isModified();

    /**
     * An exception that signals that one or more exceptions occurred while a
     * buffered object tried to access its data source or if there is a problem
     * in processing a data source.
     * 
     * @author Vaadin Ltd.
     * @since 3.0
     */
    @SuppressWarnings("serial")
    public class SourceException extends RuntimeException implements
            Serializable {

        /** Source class implementing the buffered interface */
        private final Buffered source;

        /** Original cause of the source exception */
        private Throwable[] causes = {};

        /**
         * Creates a source exception that does not include a cause.
         * 
         * @param source
         *            the source object implementing the Buffered interface.
         */
        public SourceException(Buffered source) {
            this.source = source;
        }

        /**
         * Creates a source exception from a cause exception.
         * 
         * @param source
         *            the source object implementing the Buffered interface.
         * @param cause
         *            the original cause for this exception.
         */
        public SourceException(Buffered source, Throwable cause) {
            this.source = source;
            causes = new Throwable[] { cause };
        }

        /**
         * Creates a source exception from multiple causes.
         * 
         * @param source
         *            the source object implementing the Buffered interface.
         * @param causes
         *            the original causes for this exception.
         */
        public SourceException(Buffered source, Throwable[] causes) {
            this.source = source;
            this.causes = causes;
        }

        /**
         * Gets the cause of the exception.
         * 
         * @return The (first) cause for the exception, null if no cause.
         */
        @Override
        public final Throwable getCause() {
            if (causes.length == 0) {
                return null;
            }
            return causes[0];
        }

        /**
         * Gets all the causes for this exception.
         * 
         * @return throwables that caused this exception
         */
        public final Throwable[] getCauses() {
            return causes;
        }

        /**
         * Gets a source of the exception.
         * 
         * @return the Buffered object which generated this exception.
         */
        public Buffered getSource() {
            return source;
        }

    }
}
