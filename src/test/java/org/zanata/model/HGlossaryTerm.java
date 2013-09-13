/*
 * Copyright 2011, Red Hat, Inc. and individual contributors
 * as indicated by the @author tags. See the copyright.txt file in the
 * distribution for a full listing of individual contributors.
 *
 * This is free software; you can redistribute it and/or modify it
 * under the terms of the GNU Lesser General Public License as
 * published by the Free Software Foundation; either version 2.1 of
 * the License, or (at your option) any later version.
 *
 * This software is distributed in the hope that it will be useful,
 * but WITHOUT ANY WARRANTY; without even the implied warranty of
 * MERCHANTABILITY or FITNESS FOR A PARTICULAR PURPOSE. See the GNU
 * Lesser General Public License for more details.
 *
 * You should have received a copy of the GNU Lesser General Public
 * License along with this software; if not, write to the Free
 * Software Foundation, Inc., 51 Franklin St, Fifth Floor, Boston, MA
 * 02110-1301 USA, or see the FSF site: http://www.fsf.org.
 */
package org.zanata.model;

import java.util.ArrayList;
import java.util.List;
import javax.persistence.CascadeType;
import javax.persistence.Entity;
import javax.persistence.FetchType;
import javax.persistence.JoinColumn;
import javax.persistence.ManyToOne;
import javax.persistence.OneToMany;
import javax.validation.constraints.NotNull;

import org.hibernate.annotations.Cascade;
import org.hibernate.annotations.IndexColumn;
import org.hibernate.annotations.NaturalId;
import org.hibernate.annotations.Type;

import lombok.EqualsAndHashCode;
import lombok.NoArgsConstructor;
import lombok.Setter;
import lombok.ToString;

/**
 * 
 * @author Alex Eng <a href="mailto:aeng@redhat.com">aeng@redhat.com</a>
 * 
 **/
@Entity
@Setter
@NoArgsConstructor
@EqualsAndHashCode(callSuper = true, doNotUseGetters = true, exclude = "glossaryEntry")
@ToString(doNotUseGetters = true)
public class HGlossaryTerm extends ModelEntityBase
{
   private static final long serialVersionUID = 1854278563597070432L;
   private String content;
   private List<HTermComment> comments;
   private HGlossaryEntry glossaryEntry;
   private HLocale locale;

   public HGlossaryTerm(String content)
   {
      setContent(content);
   }

   @NotNull
   @Type(type = "text")
   public String getContent()
   {
      return content;
   }

   @OneToMany(cascade = CascadeType.ALL)
   @Cascade(org.hibernate.annotations.CascadeType.DELETE_ORPHAN)
   @IndexColumn(name = "pos", base = 0, nullable = false)
   @JoinColumn(name = "glossaryTermId", nullable = false)
   public List<HTermComment> getComments()
   {
      if (comments == null)
      {
         comments = new ArrayList<HTermComment>();
      }
      return comments;
   }

   // TODO PERF @NaturalId(mutable=false) for better criteria caching
   @NaturalId
   @ManyToOne
   @JoinColumn(name = "glossaryEntryId", nullable = false)
   public HGlossaryEntry getGlossaryEntry()
   {
      return glossaryEntry;
   }

   // TODO PERF @NaturalId(mutable=false) for better criteria caching
   @NaturalId
   @ManyToOne(fetch = FetchType.LAZY)
   @JoinColumn(name = "localeId", nullable = false)
   public HLocale getLocale()
   {
      return locale;
   }
}
