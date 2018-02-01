package org.nrg.xnat.eventservice.actions;

import com.google.common.base.Joiner;
import com.google.common.base.Splitter;
import org.nrg.xft.security.UserI;
import org.nrg.xnat.eventservice.model.Action;
import org.nrg.xnat.eventservice.model.ActionAttributeConfiguration;
import org.nrg.xnat.eventservice.services.EventService;
import org.nrg.xnat.eventservice.services.EventServiceActionProvider;
import org.slf4j.Logger;
import org.slf4j.LoggerFactory;

import java.util.ArrayList;
import java.util.Arrays;
import java.util.List;
import java.util.Map;

public abstract class SingleActionProvider implements  EventServiceActionProvider {

    private static final Logger log = LoggerFactory.getLogger(EventService.class);

    public abstract Map<String, ActionAttributeConfiguration> getAttributes();

    public Action getAction() {
        return Action.builder().id(getName())
                        .actionKey(getActionKey() )
                        .displayName(getDisplayName())
                        .description(getDescription())
                        .provider(this)
                        .attributes(getAttributes())
                        .build();

    }

    @Override
    public String getName() { return this.getClass().getName(); }

    @Override
    public List<Action> getAllActions() {
        return new ArrayList<>(Arrays.asList(getAction()));
    }

    @Override
    public List<Action> getActions(UserI user) {
        return getAllActions();
    }

    @Override
    public List<Action> getActions(String xnatType, UserI user) {
        return getAllActions();
    }

    @Override
    public List<Action> getActions(String projectId, String xnatType, UserI user) {
        return getAllActions();
    }

    @Override
    public Boolean isActionAvailable(String actionKey, String projectId, String xnatType, UserI user) {
        return getAction().actionKey().contentEquals(actionKey);
    }

    public String getActionKey() {
        return actionIdToActionKey(this.getName());
    }

    @Override
    public String actionKeyToActionId(String actionKey) {
        return Splitter.on(':').splitToList(actionKey).get(0);
    }

    @Override
    public String actionIdToActionKey(String actionId) { return Joiner.on(':').join(this.getName(), actionId); }
}