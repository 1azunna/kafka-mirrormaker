package com.zunnet.mirrormaker.replicationpolicies;

import static org.junit.jupiter.api.Assertions.assertEquals;

import java.util.HashMap;
import java.util.Map;

import org.junit.jupiter.api.BeforeEach;
import org.junit.jupiter.api.Test;

public class TopicRemappingPolicyTest {

    private TopicRemappingPolicy topicRemappingPolicy;
    private static Map<String, String> props;

    @BeforeEach
    public void setUp() {
        topicRemappingPolicy = new TopicRemappingPolicy();
        props = new HashMap<>();
        props.put("source.cluster.alias", "source-cluster");
    }

    @Test
    public void testBasicTopicMapping() {
        props.put("replication.policy.topic-remapping.regex-patterns", "^mytopic\\.(\\w+)-(\\w+)\\.topic$:renamed_$1_mytopic");
        topicRemappingPolicy.configure(props);
        String sourceTopic = "mytopic.something-else.topic";
        String expectedTargetTopic = "renamed_something_mytopic";
        String actualTargetTopic = topicRemappingPolicy.formatRemoteTopic("source-cluster", sourceTopic);
        assertEquals(expectedTargetTopic, actualTargetTopic);
    }

    @Test
    public void testNonMatchingTopicMapping() {
        props.put("replication.policy.topic-remapping.regex-patterns", "^mytopic\\.(\\w+)-(\\w+)\\.topic$:renamed_$1_mytopic");
        topicRemappingPolicy.configure(props);
        String sourceTopic = "notmytopic.something-else.topic";
        String expectedTargetTopic = null;
        String actualTargetTopic = topicRemappingPolicy.formatRemoteTopic("source-cluster", sourceTopic);
        assertEquals(expectedTargetTopic, actualTargetTopic);
    }

    @Test
    public void testHeartbeatsTopicMapping() {
        props.put("replication.policy.topic-remapping.regex-patterns", "^mytopic\\.(\\w+)-(\\w+)\\.topic$:renamed_$1_mytopic");
        topicRemappingPolicy.configure(props);
        String sourceTopic = "heartbeats";
        String expectedTargetTopic = "source-cluster.heartbeats";
        String actualTargetTopic = topicRemappingPolicy.formatRemoteTopic("source-cluster", sourceTopic);
        assertEquals(expectedTargetTopic, actualTargetTopic);
    }

    @Test
    public void testOneToOneTopicMapping() {
        props.put("replication.policy.topic-remapping.regex-patterns", "(.*):$1");
        topicRemappingPolicy.configure(props);
        String sourceTopic = "mytopic";
        String expectedTargetTopic = "mytopic";
        String actualTargetTopic = topicRemappingPolicy.formatRemoteTopic("source-cluster", sourceTopic);
        assertEquals(expectedTargetTopic, actualTargetTopic);
    }

    @Test
    public void testNoRegexPatterns() {
        props.put("replication.policy.topic-remapping.regex-patterns", "");
        topicRemappingPolicy.configure(props);
        String sourceTopic = "my.topic";
        String expectedTargetTopic = "my.topic";
        String actualTargetTopic = topicRemappingPolicy.formatRemoteTopic("source-cluster", sourceTopic);
        assertEquals(expectedTargetTopic, actualTargetTopic);
    }
}
