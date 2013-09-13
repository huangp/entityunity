/*
 * Copyright 2013, Red Hat, Inc. and individual contributors
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

import javax.annotation.Nonnull;
import javax.annotation.Nullable;

import org.zanata.common.HasContents;

/**
 * @author Sean Flanigan <a href="mailto:sflaniga@redhat.com">sflaniga@redhat.com</a>
 *
 */
public interface ITextFlow extends HasContents
{
   public @Nonnull
   org.zanata.common.LocaleId getLocale();
   public @Nonnull
   java.lang.String getQualifiedId();
   /**
    * Gets the associated TargetContents for a single locale for this SourceContents.
    * Note that default implementation in HTextFlow requires a lot of database I/O
    * @param localeId
    * @return
    */
   public @Nullable
   org.zanata.model.ITextFlowTarget getTargetContents(@Nonnull org.zanata.common.LocaleId localeId);
   /**
    * Gets the associated TargetContents for all available locales for this SourceContents.
    * @return
    */
   public @Nonnull
   java.lang.Iterable<ITextFlowTarget> getAllTargetContents();
}
