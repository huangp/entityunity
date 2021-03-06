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

import lombok.EqualsAndHashCode;
import lombok.NoArgsConstructor;
import lombok.Setter;
import lombok.ToString;
import org.hibernate.annotations.NaturalId;
import org.hibernate.annotations.Type;
import org.hibernate.annotations.TypeDef;
import org.zanata.model.type.LocaleIdType;

import javax.annotation.Nonnull;
import javax.persistence.CascadeType;
import javax.persistence.Entity;
import javax.persistence.JoinColumn;
import javax.persistence.JoinTable;
import javax.persistence.ManyToMany;
import javax.persistence.OneToMany;
import javax.validation.constraints.NotNull;
import java.io.Serializable;
import java.util.HashSet;
import java.util.Set;

@Entity
@TypeDef(name = "localeId", typeClass = LocaleIdType.class)
@Setter
@NoArgsConstructor
@ToString(of = {"localeId"}, doNotUseGetters = true)
@EqualsAndHashCode(callSuper = false, of = {"localeId"}, doNotUseGetters = true)
public class HLocale extends ModelEntityBase implements Serializable {
    private static final long serialVersionUID = 1L;
    private
    @Nonnull
    org.zanata.common.LocaleId localeId;
    private boolean active;
    private boolean enabledByDefault;
    private Set<HProject> supportedProjects;
    private Set<HProjectIteration> supportedIterations;
    private Set<HLocaleMember> members;

    public HLocale(@Nonnull org.zanata.common.LocaleId localeId) {
        this.localeId = localeId;
    }

    // TODO PERF @NaturalId(mutable=false) for better criteria caching
    @SuppressWarnings("null")
    @NaturalId
    @NotNull
    @Type(type = "localeId")
    public
    @Nonnull
    org.zanata.common.LocaleId getLocaleId() {
        return localeId;
    }

    public boolean isActive() {
        return active;
    }

    public boolean isEnabledByDefault() {
        return enabledByDefault;
    }

    public void setEnabledByDefault(boolean enabledByDefault) {
        this.enabledByDefault = enabledByDefault;
    }

    @OneToMany(cascade = CascadeType.ALL, mappedBy = "id.supportedLanguage")
    public Set<HLocaleMember> getMembers() {
        if (this.members == null) {
            this.members = new HashSet<HLocaleMember>();
        }
        return this.members;
    }

    @ManyToMany
    @JoinTable(name = "HProject_Locale", joinColumns = @JoinColumn(name = "localeId"), inverseJoinColumns = @JoinColumn(name = "projectId"))
    public Set<HProject> getSupportedProjects() {
        if (supportedProjects == null)
            supportedProjects = new HashSet<HProject>();
        return supportedProjects;
    }

    @ManyToMany
    @JoinTable(name = "HProjectIteration_Locale", joinColumns = @JoinColumn(name = "localeId"), inverseJoinColumns = @JoinColumn(name = "projectIterationId"))
    public Set<HProjectIteration> getSupportedIterations() {
        if (supportedIterations == null)
            supportedIterations = new HashSet<HProjectIteration>();
        return supportedIterations;
    }

}
