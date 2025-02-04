/*
 * Licensed to the Apache Software Foundation (ASF) under one or more
 * contributor license agreements. See the NOTICE file distributed with
 * this work for additional information regarding copyright ownership.
 * The ASF licenses this file to You under the Apache License, Version 2.0
 * (the "License"); you may not use this file except in compliance with
 * the License. You may obtain a copy of the License at
 *
 * http://www.apache.org/licenses/LICENSE-2.0
 *
 * Unless required by applicable law or agreed to in writing, software
 * distributed under the License is distributed on an "AS IS" BASIS,
 * WITHOUT WARRANTIES OR CONDITIONS OF ANY KIND, either express or implied.
 * See the License for the specific language governing permissions and
 * limitations under the License.
 */

package org.apache.inlong.audit.sink;

import com.google.common.base.Charsets;
import org.apache.flume.Channel;
import org.apache.flume.Context;
import org.apache.flume.Event;
import org.apache.flume.Sink;
import org.apache.flume.Transaction;
import org.apache.flume.channel.MemoryChannel;
import org.apache.flume.conf.Configurables;
import org.apache.flume.event.EventBuilder;
import org.apache.flume.lifecycle.LifecycleController;
import org.apache.flume.lifecycle.LifecycleState;
import org.junit.Assert;
import org.junit.Before;
import org.junit.Test;
import org.powermock.api.mockito.PowerMockito;

public class KafkaSinkTest {

    private KafkaSink kafkaSink;
    private Channel channel;
    private Context context;

    @Before
    public void setUp() throws Exception {
        kafkaSink = PowerMockito.mock(KafkaSink.class);
        PowerMockito.doNothing().when(kafkaSink, "start");
        PowerMockito.when(kafkaSink.process()).thenReturn(Sink.Status.READY);
        PowerMockito.when(kafkaSink.getLifecycleState()).thenReturn(LifecycleState.ERROR);
        channel = new MemoryChannel();
        context = new Context();

        context.put("topic", "inlong-audit");
        context.put("master-host-port-list", "127.0.0.1:8080");

        kafkaSink.setChannel(channel);
        Configurables.configure(kafkaSink, context);
        Configurables.configure(channel, context);

    }

    @Test
    public void testProcess() throws InterruptedException {
        Event event = EventBuilder.withBody("test", Charsets.UTF_8);
        kafkaSink.start();
        Assert.assertTrue(LifecycleController.waitForOneOf(kafkaSink,
                LifecycleState.START_OR_ERROR, 5000));
        Transaction transaction = channel.getTransaction();

        transaction.begin();
        for (int i = 0; i < 10; i++) {
            channel.put(event);
        }
        transaction.commit();
        transaction.close();

        for (int i = 0; i < 5; i++) {
            Sink.Status status = kafkaSink.process();
            Assert.assertEquals(Sink.Status.READY, status);
        }

        kafkaSink.stop();
        Assert.assertTrue(LifecycleController.waitForOneOf(kafkaSink,
                LifecycleState.STOP_OR_ERROR, 5000));
    }
}
