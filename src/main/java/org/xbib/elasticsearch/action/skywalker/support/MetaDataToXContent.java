
package org.xbib.elasticsearch.action.skywalker.support;

import org.elasticsearch.cluster.metadata.AliasMetaData;
import org.elasticsearch.cluster.metadata.IndexMetaData;
import org.elasticsearch.cluster.metadata.IndexTemplateMetaData;
import org.elasticsearch.cluster.metadata.MappingMetaData;
import org.elasticsearch.cluster.metadata.MetaData;
import org.elasticsearch.common.xcontent.ToXContent;
import org.elasticsearch.common.xcontent.XContentBuilder;

import java.io.IOException;
import java.util.Map;

/**
 * Metadata to XContent
 */
public class MetaDataToXContent implements ToXContent {

    MetaData metadata;

    public MetaDataToXContent metadata(MetaData metadata) {
        this.metadata = metadata;
        return this;
    }

    @Override
    public XContentBuilder toXContent(XContentBuilder builder, Params params) throws IOException {
        builder.startObject();
        builder.field("version", metadata.version());
        builder.field("numberOfShards", metadata.numberOfShards());
        builder.startArray("concreteIndices");
        for (String index : metadata.concreteAllIndicesAsSet()) {
            builder.value(index);
        }
        builder.endArray();
        Map<String, Map<String, AliasMetaData>> aliases = metadata.getAliases();
        builder.startArray("aliases");
        for (String alias : aliases.keySet()) {
            builder.startObject(alias);
            builder.startArray("aliasMetadata");
            for (String s : aliases.get(alias).keySet()) {
                builder.startObject();
                AliasMetaData amd = aliases.get(alias).get(s);
                builder.field("alias", amd.getAlias());
                builder.field("filter", amd.getFilter().string());
                builder.field("indexRouting", amd.getIndexRouting());
                builder.field("searchRouting", amd.getSearchRouting());
                builder.endObject();
            }
            builder.endArray();
            builder.endObject();
        }
        builder.endArray();
        builder.startArray("indexes");
        Map<String,IndexMetaData> indices = metadata.getIndices();
        for (String s : indices.keySet()) {
            IndexMetaData imd = indices.get(s);
            builder.startObject();
            builder.field("index", imd.getIndex());
            builder.field("state", imd.getState().name());
            builder.field("numberOfReplicas", imd.getNumberOfReplicas());
            builder.field("numberOfShards", imd.getNumberOfShards());
            builder.field("totalNumberOfShards", imd.getTotalNumberOfShards());
            builder.field("version", imd.getVersion());
            builder.field("settings", imd.getSettings().getAsMap());
            Map<String, MappingMetaData> m = imd.getMappings();
            // skip mappings here
            builder.endObject();
        }
        builder.endArray();
        builder.startArray("templates");
        Map<String,IndexTemplateMetaData> templates = metadata.getTemplates();
        for (String s : templates.keySet()) {
            IndexTemplateMetaData itmd = templates.get(s);
            itmd.getName();
            itmd.getOrder();
            itmd.getTemplate();
            itmd.getSettings();
            itmd.getMappings();
        }
        builder.endArray();
        builder.endObject();
        return builder;
    }
}
