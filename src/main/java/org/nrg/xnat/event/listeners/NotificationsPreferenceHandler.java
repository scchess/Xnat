package org.nrg.xnat.event.listeners;

import org.apache.commons.logging.Log;
import org.apache.commons.logging.LogFactory;
import org.nrg.prefs.events.AbstractPreferenceHandler;
import org.nrg.xdat.preferences.NotificationsPreferences;
import org.nrg.xdat.preferences.PreferenceEvent;
import org.nrg.xdat.preferences.SiteConfigPreferences;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.context.annotation.Lazy;
import org.springframework.jdbc.core.JdbcTemplate;
import org.springframework.stereotype.Service;
import reactor.bus.EventBus;

import javax.inject.Inject;

@Service
public class NotificationsPreferenceHandler extends AbstractPreferenceHandler<PreferenceEvent> {

	@Inject
	public NotificationsPreferenceHandler(final EventBus eventBus){
		super(NotificationsPreferences.NOTIFICATIONS_TOOL_ID, eventBus);
	}

	@Autowired
	@Lazy
	private JdbcTemplate _template;

	private static final Log _log = LogFactory.getLog(NotificationsPreferenceHandler.class);


}