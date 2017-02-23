/*
 * Copyright 2000-2017 Vaadin Ltd.
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
package com.vaadin.hummingbird.uitest.ui;

import org.junit.Assert;
import org.junit.Test;
import org.openqa.selenium.By;
import org.openqa.selenium.WebElement;

import com.vaadin.hummingbird.testutil.PhantomJSTest;

public class RerouteIT extends PhantomJSTest {

    @Test
    public void testReroutingToErrorView() {
        open();
        WebElement checkbox = findElement(By.id("check"))
                .findElement(By.tagName("input"));
        checkbox.click();

        findElement(By.id("navigate")).click();

        Assert.assertNotNull("Could not find error page text", findElement(
                By.xpath("//div[contains(.,'This is the error view.')]")));
    }

    @Test
    public void testViewWithoutRerouting() {
        open();
        findElement(By.id("navigate")).click();

        Assert.assertNotNull("Navigate button was not found",
                findElement(By.id("navigate")));
    }
}
