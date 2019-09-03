

package io.archilab.prox.projectservice.project;

import lombok.AccessLevel;
import lombok.Data;
import lombok.NoArgsConstructor;
import lombok.Setter;

import javax.persistence.Embeddable;

@Embeddable
@Data
@Setter(AccessLevel.NONE)
@NoArgsConstructor(access = AccessLevel.PROTECTED)
public class ProjectRequirement {

    private String requirement;

    public ProjectRequirement(String requirement) {
        this.requirement = requirement;
    }
}
