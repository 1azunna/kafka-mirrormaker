package com.zunnet.mirrormaker.replicationpolicies;

import org.apache.kafka.connect.mirror.DefaultReplicationPolicy;
import org.slf4j.Logger;
import org.slf4j.LoggerFactory;

import java.util.ArrayList;
import java.util.List;
import java.util.Map;
import java.util.regex.Matcher;
import java.util.regex.Pattern;

public class TopicRemappingPolicy extends DefaultReplicationPolicy {

    private static final Logger logger = LoggerFactory.getLogger(TopicRemappingPolicy.class);
    private String sourceClusterAlias;
    private List<PatternReplacement> patternReplacements = new ArrayList<>();

    @Override
    public void configure(Map<String, ?> props) {
        // Load source cluster alias from props.
        sourceClusterAlias = props.get("source.cluster.alias").toString();
        String mappingConfig = props.get("replication.policy.topic-remapping.regex-patterns").toString();
        
        // Parse the mapping configuration
        if (!mappingConfig.isEmpty()) {
            String[] mappings = mappingConfig.split("\\|");
            for (String mapping : mappings) {
                String[] parts = mapping.split(":");
                if (parts.length == 2) {
                    patternReplacements.add(new PatternReplacement(parts[0], parts[1]));
                } else {
                    logger.warn("Invalid mapping format: {}", mapping);
                }
            }
        }
    }

    @Override
    public String formatRemoteTopic(String sourceClusterAlias, String topic) {
        // Always replicate internal topics (e.g., starting with "__") or heartbeat topics
        if (looksLikeHeartbeat(topic) || isMM2InternalTopic(topic) || isCheckpointsTopic(topic)) {
            return super.formatRemoteTopic(sourceClusterAlias, topic); // Use default formatting for these topics
        }

        if (patternReplacements.isEmpty()) {
            return topic;  // Default behavior: return the original topic name if replication.policy.topic-remapping.regex-patterns is not defined.
        }

        for (PatternReplacement pr : patternReplacements) {
            Matcher matcher = pr.pattern.matcher(topic);
            if (matcher.matches()) {
                String newTopic = matcher.replaceAll(pr.replacement);
                logger.info("Mapping topic '{}' to '{}'", topic, newTopic);
                return newTopic;
            }
        }
        logger.info("Topic '{}' does not match any pattern and will not be replicated.", topic);
        return null;
    }

    @Override
    public String topicSource(String topic) {
        return topic == null ? null : sourceClusterAlias;
    }

    @Override
    public String upstreamTopic(String topic) {
        return null; // Default behavior, can be customized if needed
    }

    private static class PatternReplacement {
        Pattern pattern;
        String replacement;

        PatternReplacement(String pattern, String replacement) {
            this.pattern = Pattern.compile(pattern);
            this.replacement = replacement;
        }
    }

    private boolean looksLikeHeartbeat(String topic) {
        return topic != null && topic.endsWith(heartbeatsTopic());
    }
}
