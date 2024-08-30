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
    public void testShopCartruleStreamTopicMapping() {
        props.put("replication.policy.topic-remapping.regex-patterns", "^mytopic\\.(\\w+)-(\\w+)\\.topic$:renamed_$1_mytopic");
        topicRemappingPolicy.configure(props);
        String sourceTopic = "mytopic.something-else.topic";
        String expectedTargetTopic = "renamed_something_mytopic";
        String actualTargetTopic = topicRemappingPolicy.formatRemoteTopic("source-cluster", sourceTopic);
        assertEquals(expectedTargetTopic, actualTargetTopic);
    }
}
