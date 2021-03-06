package com.wyfinger.icleboir;

import android.content.Context;
import android.os.Parcel;
import android.support.test.InstrumentationRegistry;
import android.support.test.runner.AndroidJUnit4;

import org.junit.Test;
import org.junit.runner.RunWith;

import static org.junit.Assert.*;

/**
 * Instrumentation test, which will execute on an Android device.
 *
 * @see <a href="http://d.android.com/tools/testing">Testing documentation</a>
 */
@RunWith(AndroidJUnit4.class)
public class ExampleInstrumentedTest {

    @Test
    public void useAppContext() throws Exception {
        // Context of the app under test.
        Context appContext = InstrumentationRegistry.getTargetContext();

        assertEquals("com.wyfinger.icleboir", appContext.getPackageName());
    }

    @Test
    public void actionDaysParcelable() throws Exception {

        Parcel parcel = Parcel.obtain();

        ActionDays sourceActionDays = new ActionDays(true, false, false, false, false,true,false);

        sourceActionDays.writeToParcel(parcel, 0);
        parcel.setDataPosition(0);

        ActionDays destActionDays = (ActionDays) ActionDays.CREATOR.createFromParcel(parcel);

        assertEquals(destActionDays.isEveryday(), sourceActionDays.isEveryday());
        assertEquals(destActionDays.isWorkdays(), sourceActionDays.isWorkdays());
        assertEquals(destActionDays.isWeekend(), sourceActionDays.isWeekend());
        assertEquals(destActionDays.mon, sourceActionDays.mon);
        assertEquals(destActionDays.tue, sourceActionDays.tue);
        assertEquals(destActionDays.wed, sourceActionDays.wed);
        assertEquals(destActionDays.thu, sourceActionDays.thu);
        assertEquals(destActionDays.fri, sourceActionDays.fri);
        assertEquals(destActionDays.sat, sourceActionDays.sat);
        assertEquals(destActionDays.sun, sourceActionDays.sun);

    }
}
