/*
 * Copyright 2013, Red Hat, Inc. and individual contributors as indicated by the
 * @author tags. See the copyright.txt file in the distribution for a full
 * listing of individual contributors.
 * 
 * This is free software; you can redistribute it and/or modify it under the
 * terms of the GNU Lesser General Public License as published by the Free
 * Software Foundation; either version 2.1 of the License, or (at your option)
 * any later version.
 * 
 * This software is distributed in the hope that it will be useful, but WITHOUT
 * ANY WARRANTY; without even the implied warranty of MERCHANTABILITY or FITNESS
 * FOR A PARTICULAR PURPOSE. See the GNU Lesser General Public License for more
 * details.
 * 
 * You should have received a copy of the GNU Lesser General Public License
 * along with this software; if not, write to the Free Software Foundation,
 * Inc., 51 Franklin St, Fifth Floor, Boston, MA 02110-1301 USA, or see the FSF
 * site: http://www.fsf.org.
 */
package org.zanata.model.tm;

import com.google.common.collect.Maps;
import lombok.AccessLevel;
import lombok.Data;
import lombok.EqualsAndHashCode;
import lombok.NoArgsConstructor;
import lombok.Setter;
import lombok.ToString;
import org.zanata.model.ModelEntityBase;

import javax.annotation.Nonnull;
import javax.persistence.Access;
import javax.persistence.AccessType;
import javax.persistence.Basic;
import javax.persistence.Column;
import javax.persistence.Entity;
import javax.persistence.EnumType;
import javax.persistence.Enumerated;
import javax.persistence.FetchType;
import java.util.Map;

/**
 * A translation unit variant.
 * This is the equivalent of a translated string.
 *
 * @author Carlos Munoz <a href="mailto:camunoz@redhat.com">camunoz@redhat.com</a>
 */
@Entity
@EqualsAndHashCode(callSuper = true, exclude = {"content"})
@ToString(exclude = {"contentHash", "plainTextSegmentHash"})
@NoArgsConstructor
@Data
@Access(AccessType.FIELD)
public class TransMemoryUnitVariant extends ModelEntityBase implements HasTMMetadata {
    private static final long serialVersionUID = 1L;

    // This is the BCP-47 language code
    @Column(nullable = false)
    private String language;

    @Column(name = "tagged_segment", nullable = false, length = Integer.MAX_VALUE)
    private String taggedSegment;

    @Setter(AccessLevel.NONE)
    @Column(name = "plain_text_segment", nullable = false, length = Integer.MAX_VALUE)
    private String plainTextSegment;

    @Setter(AccessLevel.PROTECTED)
    @Column(name = "plain_text_segment_hash", nullable = false)
    private String plainTextSegmentHash;

    @Enumerated(EnumType.STRING)
    @Column(name = "metadata_type", nullable = true)
    private org.zanata.model.tm.TMMetadataType metadataType;

    @Column(nullable = true)
    @Basic(fetch = FetchType.LAZY)
    private String metadata;

    public static org.zanata.model.tm.TransMemoryUnitVariant tuv(String language, String content) {
        return new org.zanata.model.tm.TransMemoryUnitVariant(language, content);
    }

    public TransMemoryUnitVariant(String language, String taggedSegment) {
        this.language = language;
        this.setTaggedSegment(taggedSegment);
    }

    public void setTaggedSegment(String taggedSegment) {
        this.taggedSegment = taggedSegment;
        updatePlainTextSegment();
    }

    private void updatePlainTextSegment() {
        //      this.plainTextSegment = OkapiUtil.removeFormattingMarkup(taggedSegment);
        updatePlainTextSegmentHash();
    }

    private void updatePlainTextSegmentHash() {
        //      this.plainTextSegmentHash = HashUtil.generateHash(this.plainTextSegment);
    }

    @Override
    protected boolean logPersistence() {
        return false;
    }

    @Override
    public String getMetadata(org.zanata.model.tm.TMMetadataType tmType) {
        if (this.metadataType == tmType) {
            return this.metadata;
        }
        return null;
    }

    @Override
    public void setMetadata(@Nonnull org.zanata.model.tm.TMMetadataType tmType, String metadata) {
        assert this.metadataType == null || this.metadataType == tmType : "Only one type of metadata is supported for this entity";
        setMetadataType(tmType);
        setMetadata(metadata);
    }

    public static Map<String, org.zanata.model.tm.TransMemoryUnitVariant> newMap(org.zanata.model.tm.TransMemoryUnitVariant... tuvs) {
        Map<String, org.zanata.model.tm.TransMemoryUnitVariant> map = Maps.newHashMap();
        for (org.zanata.model.tm.TransMemoryUnitVariant target : tuvs) {
            map.put(target.getLanguage(), target);
        }
        return map;
    }

}
