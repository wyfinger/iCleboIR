package com.wyfinger.icleboir;

import android.os.Parcel;

import org.junit.Before;
import org.junit.Test;
import org.junit.internal.runners.JUnit38ClassRunner;
import org.junit.runner.RunWith;
/*import org.mockito.Mock;
import org.mockito.Mockito;
import org.mockito.internal.runners.JUnit44RunnerImpl;
import org.mockito.runners.MockitoJUnitRunner;*/

import static org.junit.Assert.*;

/**
 * Example local unit test, which will execute on the development machine (host).
 *
 * @see <a href="http://d.android.com/tools/testing">Testing documentation</a>
 */
public class ActionDaysUnitTest {

    @Test
    public void parcelable_isCorrect() throws Exception {

        /*Parcel parcel = MockParcel.obtain();

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
        assertEquals(destActionDays.sun, sourceActionDays.sun);*/

    }

    @Test
    public void addition_isCorrect() throws Exception {
        assertEquals(4, 2 + 2);
    }

}