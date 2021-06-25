/*
 * MIT License
 *
 * Copyright (c) 2020 TH Köln
 *
 * Permission is hereby granted, free of charge, to any person obtaining a copy
 * of this software and associated documentation files (the "Software"), to deal
 * in the Software without restriction, including without limitation the rights
 * to use, copy, modify, merge, publish, distribute, sublicense, and/or sell
 * copies of the Software, and to permit persons to whom the Software is
 * furnished to do so, subject to the following conditions:
 *
 * The above copyright notice and this permission notice shall be included in all
 * copies or substantial portions of the Software.
 *
 * THE SOFTWARE IS PROVIDED "AS IS", WITHOUT WARRANTY OF ANY KIND, EXPRESS OR
 * IMPLIED, INCLUDING BUT NOT LIMITED TO THE WARRANTIES OF MERCHANTABILITY,
 * FITNESS FOR A PARTICULAR PURPOSE AND NONINFRINGEMENT. IN NO EVENT SHALL THE
 * AUTHORS OR COPYRIGHT HOLDERS BE LIABLE FOR ANY CLAIM, DAMAGES OR OTHER
 * LIABILITY, WHETHER IN AN ACTION OF CONTRACT, TORT OR OTHERWISE, ARISING FROM,
 * OUT OF OR IN CONNECTION WITH THE SOFTWARE OR THE USE OR OTHER DEALINGS IN THE
 * SOFTWARE.
 */

package de.innovationhub.prox.projectservice.project;

import de.innovationhub.prox.projectservice.module.ModuleType;
import java.util.List;
import java.util.Set;
import java.util.UUID;
import org.springframework.data.rest.core.annotation.RestResource;
import org.springframework.web.bind.annotation.RequestParam;

public interface ProjectRepositoryCustom {

  List<Project> findAllByIds(@RequestParam("projectIds") UUID[] projectIds);

  @RestResource(exported = true, path = "findAvailableProjectsOfCreator")
  Set<Project> findAvailableProjectsOfCreator(final UUID creatorId);

  @RestResource(exported = true, path = "findRunningProjectsOfCreator")
  Set<Project> findRunningProjectsOfCreator(final UUID creatorId);

  @RestResource(exported = true, path = "findFinishedProjectsOfCreator")
  Set<Project> findinishedProjectsOfCreator(final UUID creatorId);

  @RestResource(exported = true, path = "findRunningAndFinishedProjectsOfCreator")
  Set<Project> findRunningAndFinishedProjectsOfCreator(final UUID creatorId);

  @RestResource(exported = true, path = "filterProjects")
  Set<Project> filterProjects(ProjectStatus status, String[] moduleTypeKeys, String text);

  @RestResource(exported = true, path = "findProjectStatsOfCreator")
  ProjectStats findProjectStatsOfCreator(final UUID creatorId);
}
