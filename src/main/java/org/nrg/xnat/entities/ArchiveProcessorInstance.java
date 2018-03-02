package org.nrg.xnat.entities;

import lombok.extern.slf4j.Slf4j;
import org.hibernate.annotations.Cache;
import org.hibernate.annotations.CacheConcurrencyStrategy;
import org.nrg.framework.orm.hibernate.AbstractHibernateEntity;

import javax.persistence.*;
import java.util.List;
import java.util.Map;

/**
 * Created by mike on 2/26/18.
 */
@Slf4j
@Entity
@Table
@Cache(usage = CacheConcurrencyStrategy.READ_WRITE, region = "nrg")
public class ArchiveProcessorInstance extends AbstractHibernateEntity {
    public ArchiveProcessorInstance(){

    }

    public ArchiveProcessorInstance(String scope, List<String> projectIdsList, int priority, Map<String, String> parameters, String processorClass) {
        this._scope = scope;
        this._projectIdsList = projectIdsList;
        this._priority = priority;
        this._parameters = parameters;
        this._processorClass = processorClass;
    }

    public String getScope() {
        return _scope;
    }

    public void setScope(String scope) {
        this._scope = scope;
    }

    @ElementCollection
    public List<String> getProjectIdsList() {
        return _projectIdsList;
    }

    public void setProjectIdsList(List<String> projectIdsList) {
        this._projectIdsList = projectIdsList;
    }

    public int getPriority() {
        return _priority;
    }

    public void setPriority(int priority) {
        this._priority = priority;
    }

    @ElementCollection
    @MapKeyColumn(name="parameterName")
    @Column(name="value")
    public Map<String, String> getParameters() {
        return _parameters;
    }

    public void setParameters(Map<String, String> parameters) {
        this._parameters = parameters;
    }

    public String getProcessorClass() {
        return _processorClass;
    }

    public void setProcessorClass(String processorClass) {
        this._processorClass = processorClass;
    }

    private String _scope;
    private List<String> _projectIdsList;
    private int _priority;
    private Map<String, String> _parameters;
    private String _processorClass;

    public static final String SITE_SCOPE="site";
}
