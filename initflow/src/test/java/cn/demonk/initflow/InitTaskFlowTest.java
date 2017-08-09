package cn.demonk.initflow;

import android.text.TextUtils;

import org.junit.After;
import org.junit.Before;
import org.junit.Test;
import org.junit.runner.RunWith;
import org.mockito.Mockito;
import org.mockito.invocation.InvocationOnMock;
import org.mockito.stubbing.Answer;
import org.powermock.api.mockito.PowerMockito;
import org.powermock.core.classloader.annotations.PrepareForTest;
import org.powermock.modules.junit4.PowerMockRunner;

import cn.demonk.initflow.utils.L;

/**
 * Created by ligs on 8/10/17.
 */
@RunWith(PowerMockRunner.class)
@PrepareForTest({TextUtils.class, L.class})
public class InitTaskFlowTest {
    @Before
    public void setUp() throws Exception {

        PowerMockito.mockStatic(TextUtils.class);
        PowerMockito.mockStatic(L.class);
        PowerMockito.when(TextUtils.isEmpty(Mockito.any(CharSequence.class))).thenAnswer(new Answer<Object>() {
            @Override
            public Object answer(InvocationOnMock invocation) throws Throwable {
                String text = (String) invocation.getArguments()[0];
                return text == null || text.length() == 0;
            }
        });

        PowerMockito.doAnswer(new Answer<Object>() {
            @Override
            public Object answer(InvocationOnMock invocation) throws Throwable {
                for (Object obj : invocation.getArguments()) {
                    System.out.println(obj.toString());
                }
                return null;
            }
        }).when(L.class, "d", Mockito.anyString());
    }

    @After
    public void tearDown() throws Exception {

    }

    @Test
    public void run() throws Exception {
        TestClass testObj = new TestClass();
        InitTaskFlow.instance().run(testObj, TestClass.EIGHT);
    }

}