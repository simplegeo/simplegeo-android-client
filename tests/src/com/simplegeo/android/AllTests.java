/**
 * Copyright (c) 2009-2010, SimpleGeo
 * All rights reserved.
 *
 * Redistribution and use in source and binary forms, with or without 
 * modification, are permitted provided that the following conditions are met:
 *
 * Redistributions of source code must retain the above copyright notice, 
 * this list of conditions and the following disclaimer. Redistributions 
 * in binary form must reproduce the above copyright notice, this list of
 * conditions and the following disclaimer in the documentation and/or 
 * other materials provided with the distribution.
 * 
 * Neither the name of the SimpleGeo nor the names of its contributors may
 * be used to endorse or promote products derived from this software 
 * without specific prior written permission.
 *  
 * THIS SOFTWARE IS PROVIDED BY THE COPYRIGHT HOLDERS AND CONTRIBUTORS "AS IS"
 * AND ANY EXPRESS OR IMPLIED WARRANTIES, INCLUDING, BUT NOT LIMITED TO, THE 
 * IMPLIED WARRANTIES OF MERCHANTABILITY AND FITNESS FOR A PARTICULAR PURPOSE 
 * ARE DISCLAIMED. IN NO EVENT SHALL THE COPYRIGHT HOLDER OR CONTRIBUTORS 
 * BE LIABLE FOR ANY DIRECT, INDIRECT, INCIDENTAL, SPECIAL, EXEMPLARY, OR 
 * CONSEQUENTIAL DAMAGES (INCLUDING, BUT NOT LIMITED TO, PROCUREMENT OF SUBSTITUTE 
 * GOODS OR SERVICES; LOSS OF USE, DATA, OR PROFITS; OR BUSINESS INTERRUPTION) HOWEVER 
 * CAUSED AND ON ANY THEORY OF LIABILITY, WHETHER IN CONTRACT, STRICT LIABILITY, OR TORT
 * (INCLUDING NEGLIGENCE OR OTHERWISE) ARISING IN ANY WAY OUT OF THE USE OF THIS SOFTWARE, 
 * EVEN IF ADVISED OF THE POSSIBILITY OF SUCH DAMAGE.
 */
package com.simplegeo.android;

import junit.framework.Test;
import junit.framework.TestSuite;

import android.test.suitebuilder.TestSuiteBuilder;

/**
 * A test suite containing all tests for ApiDemos.
 *
 * To run all suites found in this apk:
 * $ adb shell am instrument -w \
 *   com.example.android.apis.tests/android.test.InstrumentationTestRunner
 *
 * To run just this suite from the command line:
 * $ adb shell am instrument -w \
 *   -e class com.example.android.apis.AllTests \
 *   com.example.android.apis.tests/android.test.InstrumentationTestRunner
 *
 * To run an individual test case, e.g. {@link com.example.android.apis.os.MorseCodeConverterTest}:
 * $ adb shell am instrument -w \
 *   -e class com.example.android.apis.os.MorseCodeConverterTest \
 *   com.example.android.apis.tests/android.test.InstrumentationTestRunner
 *
 * To run an individual test, e.g. {@link com.example.android.apis.os.MorseCodeConverterTest#testCharacterS()}:
 * $ adb shell am instrument -w \
 *   -e class com.example.android.apis.os.MorseCodeConverterTest#testCharacterS \
 *   com.example.android.apis.tests/android.test.InstrumentationTestRunner
 */
public class AllTests extends TestSuite {

    public static Test suite() {
        return new TestSuiteBuilder(AllTests.class)
                .includeAllPackagesUnderHere()
                .build();
    }
}
