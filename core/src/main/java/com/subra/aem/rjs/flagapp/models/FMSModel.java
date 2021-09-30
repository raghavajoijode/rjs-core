package com.subra.aem.rjs.flagapp.models;

import org.subra.commons.dtos.flagapp.Flag;
import org.subra.commons.dtos.flagapp.Project;

import java.util.List;

public interface FMSModel {

    default String getProject() {
        throw new UnsupportedOperationException();
    }

    default List<Project> getProjects() {
        throw new UnsupportedOperationException();
    }

    default List<Flag> getFlags() {
        throw new UnsupportedOperationException();
    }

    default Object getMessage() {
        throw new UnsupportedOperationException();
    }

}
