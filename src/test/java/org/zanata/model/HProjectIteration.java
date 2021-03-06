/*
 * Copyright 2010, Red Hat, Inc. and individual contributors as indicated by the
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
package org.zanata.model;

import com.google.common.collect.ImmutableList;
import lombok.NoArgsConstructor;
import lombok.Setter;
import lombok.ToString;
import org.hibernate.annotations.NaturalId;
import org.hibernate.annotations.Type;
import org.hibernate.annotations.TypeDef;
import org.hibernate.annotations.Where;
import org.zanata.common.EntityStatus;
import org.zanata.common.ProjectType;
import org.zanata.model.type.EntityStatusType;
import org.zanata.model.type.EntityType;

import javax.persistence.CascadeType;
import javax.persistence.Column;
import javax.persistence.ElementCollection;
import javax.persistence.Entity;
import javax.persistence.EnumType;
import javax.persistence.Enumerated;
import javax.persistence.FetchType;
import javax.persistence.JoinColumn;
import javax.persistence.JoinTable;
import javax.persistence.ManyToMany;
import javax.persistence.ManyToOne;
import javax.persistence.MapKey;
import javax.persistence.OneToMany;
import javax.persistence.OrderBy;
import javax.persistence.Transient;
import javax.validation.constraints.NotNull;
import java.util.HashMap;
import java.util.HashSet;
import java.util.Iterator;
import java.util.List;
import java.util.Map;
import java.util.Set;

/**
 * @see org.zanata.rest.dto.ProjectIteration
 */
@Entity
@TypeDef(name = "entityStatus", typeClass = EntityStatusType.class)
@NoArgsConstructor
@Setter
@ToString(callSuper = true, of = {"project"})
public class HProjectIteration extends SlugEntityBase implements Iterable<DocumentWithId>, HasEntityStatus, IsEntityWithType {

    private static final long serialVersionUID = 182037127575991478L;
    private org.zanata.model.HProject project;

    private org.zanata.model.HProjectIteration parent;
    private List<org.zanata.model.HProjectIteration> children;

    private Map<String, HDocument> documents;
    private Map<String, HDocument> allDocuments;

    private boolean overrideLocales = false;
    private boolean overrideValidations = false;
    private Set<HLocale> customizedLocales;
    private Set<HIterationGroup> groups;
    private Set<String> customizedValidations;

    private ProjectType projectType;
    private Boolean requireTranslationReview = false;
    private EntityStatus status = EntityStatus.ACTIVE;

    public boolean getOverrideLocales() {
        return this.overrideLocales;
    }

    public boolean getOverrideValidations() {
        return overrideValidations;
    }

    @Column(nullable = true)
    public Boolean getRequireTranslationReview() {
        if (requireTranslationReview == null) {
            return Boolean.FALSE;
        }
        return requireTranslationReview;
    }

    @ManyToOne
    @NotNull
    // TODO PERF @NaturalId(mutable=false) for better criteria caching
    @NaturalId
    public HProject getProject() {
        return project;
    }

    @Enumerated(EnumType.STRING)
    public ProjectType getProjectType() {
        return projectType;
    }

    @ManyToMany
    @JoinTable(name = "HProjectIteration_Locale", joinColumns = @JoinColumn(name = "projectIterationId"), inverseJoinColumns = @JoinColumn(name = "localeId"))
    public Set<HLocale> getCustomizedLocales() {
        if (customizedLocales == null)
            customizedLocales = new HashSet<HLocale>();
        return customizedLocales;
    }

    @OneToMany(mappedBy = "parent")
    public List<org.zanata.model.HProjectIteration> getChildren() {
        return children;
    }

    @ManyToOne
    @JoinColumn(name = "parentId")
    public org.zanata.model.HProjectIteration getParent() {
        return parent;
    }

    @OneToMany(mappedBy = "projectIteration", cascade = CascadeType.ALL)
    @MapKey(name = "docId")
    @Where(clause = "obsolete=0")
    // TODO add an index for path, name
    @OrderBy("path, name")
    public Map<String, HDocument> getDocuments() {
        if (documents == null)
            documents = new HashMap<String, HDocument>();
        return documents;
    }

    @OneToMany(mappedBy = "projectIteration", cascade = CascadeType.ALL)
    @MapKey(name = "docId")
    // even obsolete documents
    public Map<String, HDocument> getAllDocuments() {
        if (allDocuments == null)
            allDocuments = new HashMap<String, HDocument>();
        return allDocuments;
    }

    @ManyToMany
    @JoinTable(name = "HIterationGroup_ProjectIteration", joinColumns = @JoinColumn(name = "projectIterationId"), inverseJoinColumns = @JoinColumn(name = "iterationGroupId"))
    public Set<HIterationGroup> getGroups() {
        if (groups == null) {
            groups = new HashSet<HIterationGroup>();
        }
        return groups;
    }

    @JoinTable(name = "HProjectIteration_Validation", joinColumns = @JoinColumn(name = "projectIterationId"))
    @Type(type = "text")
    @ElementCollection(fetch = FetchType.LAZY)
    @Column(name = "validation", nullable = false)
    public Set<String> getCustomizedValidations() {
        if (customizedValidations == null) {
            customizedValidations = new HashSet<String>();
        }
        return customizedValidations;
    }

    @Override
    public Iterator<DocumentWithId> iterator() {
        return ImmutableList.<DocumentWithId>copyOf(getDocuments().values()).iterator();
    }

    @Type(type = "entityStatus")
    @NotNull
    @Override
    public EntityStatus getStatus() {
        return status;
    }

    @Override
    @Transient
    public EntityType getEntityType() {
        return EntityType.HProjectIteration;
    }
}
