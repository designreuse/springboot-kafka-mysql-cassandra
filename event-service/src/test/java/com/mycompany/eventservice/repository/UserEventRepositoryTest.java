package com.mycompany.eventservice.repository;

import com.mycompany.eventservice.model.UserEvent;
import com.mycompany.eventservice.model.UserEventKey;
import org.cassandraunit.spring.CassandraDataSet;
import org.cassandraunit.spring.CassandraUnit;
import org.cassandraunit.spring.CassandraUnitDependencyInjectionTestExecutionListener;
import org.junit.Test;
import org.junit.runner.RunWith;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.boot.test.context.SpringBootTest;
import org.springframework.test.context.TestExecutionListeners;
import org.springframework.test.context.junit4.SpringRunner;
import org.springframework.test.context.support.DependencyInjectionTestExecutionListener;

import java.util.Date;
import java.util.List;

import static com.mycompany.eventservice.util.MyLocalDateHandler.fromStringToDate;
import static org.assertj.core.api.Assertions.assertThat;

@RunWith(SpringRunner.class)
@SpringBootTest({"spring.data.cassandra.port=9142", "spring.data.cassandra.keyspace=mycompany"})
@TestExecutionListeners({CassandraUnitDependencyInjectionTestExecutionListener.class,
        DependencyInjectionTestExecutionListener.class})
@CassandraDataSet(value = {"event-service.cql"}, keyspace = "mycompany")
@CassandraUnit
public class UserEventRepositoryTest {

    @Autowired
    private UserEventRepository userEventRepository;

    @Test
    public void given_noUserEvent_when_findByKeyUserId_then_returnEmptyList() {
        List<UserEvent> userEvents = userEventRepository.findByKeyUserId(1L);
        assertThat(userEvents).hasSize(0);
    }

    @Test
    public void given_oneUserEvent_when_findByKeyUserId_then_returnListWitOneUserEvent() {
        Long userId = 1L;
        Date datetime = fromStringToDate("2018-12-03T10:15:30+0000");
        String data = "data123";
        String type = "type123";
        UserEvent userEvent = new UserEvent(new UserEventKey(userId, datetime), type, data);

        userEventRepository.save(userEvent);

        List<UserEvent> userEvents = userEventRepository.findByKeyUserId(1L);

        assertThat(userEvents).hasSize(1);
        assertThat(userEvents.get(0).getKey().getUserId()).isEqualTo(userId);
        assertThat(userEvents.get(0).getKey().getDatetime()).isEqualTo(datetime);
        assertThat(userEvents.get(0).getData()).isEqualTo(data);
        assertThat(userEvents.get(0).getType()).isEqualTo(type);
    }

    @Test
    public void given_twoUserEvents_when_findByKeyUserId_then_returnListUserEventsOrdered() {
        Long userId = 1L;
        Date datetime1 = fromStringToDate("2018-12-03T10:15:30+0000");
        String data1 = "data123";
        String type1 = "type123";
        UserEvent userEvent1 = new UserEvent(new UserEventKey(userId, datetime1), type1, data1);

        Date datetime2 = fromStringToDate("2018-12-03T10:15:31+0000");
        String data2 = "data123";
        String type2 = "type123";
        UserEvent userEvent2 = new UserEvent(new UserEventKey(userId, datetime2), type2, data2);

        userEventRepository.save(userEvent1);
        userEventRepository.save(userEvent2);

        List<UserEvent> userEvents = userEventRepository.findByKeyUserId(1L);

        assertThat(userEvents).hasSize(2);

        assertThat(userEvents.get(0).getKey().getUserId()).isEqualTo(userId);
        assertThat(userEvents.get(0).getKey().getDatetime()).isEqualTo(datetime1);
        assertThat(userEvents.get(0).getData()).isEqualTo(data1);
        assertThat(userEvents.get(0).getType()).isEqualTo(type1);

        assertThat(userEvents.get(1).getKey().getUserId()).isEqualTo(userId);
        assertThat(userEvents.get(1).getKey().getDatetime()).isEqualTo(datetime2);
        assertThat(userEvents.get(1).getData()).isEqualTo(data2);
        assertThat(userEvents.get(1).getType()).isEqualTo(type2);
    }

}