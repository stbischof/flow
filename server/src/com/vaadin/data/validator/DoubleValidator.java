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
package com.vaadin.data.validator;

/**
 * String validator for a double precision floating point number. See
 * {@link com.vaadin.data.validator.AbstractStringValidator} for more
 * information.
 * 
 * @author Vaadin Ltd.
 * @since 5.4
 * @deprecated in Vaadin 7.0. Use an Double converter on the field instead.
 */
@Deprecated
@SuppressWarnings("serial")
public class DoubleValidator extends AbstractStringValidator {

    /**
     * Creates a validator for checking that a string can be parsed as an
     * double.
     * 
     * @param errorMessage
     *            the message to display in case the value does not validate.
     * @deprecated in Vaadin 7.0. Use a Double converter on the field instead
     *             and/or use a {@link DoubleRangeValidator} for validating that
     *             the value is inside a given range.
     */
    @Deprecated
    public DoubleValidator(String errorMessage) {
        super(errorMessage);
    }

    @Override
    protected boolean isValidValue(String value) {
        try {
            Double.parseDouble(value);
            return true;
        } catch (Exception e) {
            return false;
        }
    }

    @Override
    public void validate(Object value) throws InvalidValueException {
        if (value != null && value instanceof Double) {
            // Allow Doubles to pass through the validator for easier
            // migration. Otherwise a TextField connected to an double property
            // with a DoubleValidator will fail.
            return;
        }

        super.validate(value);
    }

}
