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

package io.archilab.prox.projectservice.utils;

import io.archilab.prox.projectservice.project.Project;
import io.archilab.prox.projectservice.project.ProjectRepository;
import java.util.Optional;
import java.util.UUID;
import javax.servlet.http.HttpServletRequest;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.stereotype.Component;

/**
 * A security component which provides some useful security features and checks to secure the service
 */
@Component
public class WebSecurity {

  private final AuthenticationUtils authenticationUtils;

  @Autowired
  public WebSecurity(AuthenticationUtils authenticationUtils) {
    this.authenticationUtils = authenticationUtils;
  }

  /**
   * Check whether the requesting user equals the user who has created the project
   *
   * The users will be compared by obtaining the UUID from the request using the autowired {@link AuthenticationUtils}
   * and selecting the creator of the project
   * @param request Request which contains the requesting users principal
   * @param projectId Id of project on which the check should be performed
   * @param projectRepository Instance of projectRepository to obtain the associated project of <code>projectId</code>
   * @return <code>true</code> if requesting user and project creator are the same, otherwise <code>false</code>
   */
  public boolean checkProjectCreator(HttpServletRequest request, UUID projectId, ProjectRepository projectRepository) {
    Optional<UUID> optionalUUID = authenticationUtils.getUserUUIDFromRequest(request);
    Optional<Project> optionalProject = projectRepository.findById(projectId);
    return optionalProject.isPresent()
        && optionalProject.get().getCreatorID() != null
        && optionalProject.get().getCreatorID().getCreatorID() != null
        && optionalUUID.isPresent()
        && optionalProject.get().getCreatorID().getCreatorID().equals(optionalUUID.get());
  }
}
