package de.innovationhub.prox.projectservice.project.dto;


import de.innovationhub.prox.projectservice.project.ProjectStatus;

public record CreateProjectDto(
    String name,
    String description,
    String shortDescription,
    String requirement,
    ProjectStatus status,
    String creatorName,
    String supervisorName) {}
