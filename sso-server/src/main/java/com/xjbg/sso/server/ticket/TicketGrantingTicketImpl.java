package com.xjbg.sso.server.ticket;

import com.xjbg.sso.core.util.StringUtil;
import com.xjbg.sso.server.authencation.Principal;
import lombok.Getter;
import lombok.Setter;

import java.util.Collection;
import java.util.HashMap;
import java.util.Map;

/**
 * @author kesc
 * @since 2019/5/17
 */
@Getter
@Setter
public class TicketGrantingTicketImpl extends AbstractTicket implements TicketGrantingTicket {
    private Map<String, String> services;
    private String principal;
    private boolean onlyTrackMostRecentSession;

    public TicketGrantingTicketImpl() {
        services = new HashMap<>();
        onlyTrackMostRecentSession = true;
    }

    public TicketGrantingTicketImpl(final String id, final Principal principal, final long expireInMills) {
        this();
        this.id = id;
        this.principal = principal.getId();
        this.expireAt = expireInMills + System.currentTimeMillis();
    }

    @Override
    public void removeAllServices() {
        services.clear();
    }

    @Override
    public ServiceTicket grantServiceTicket(final String id, final String service, final long serviceTicketExpireInMills) {
        ServiceTicketImpl serviceTicket = new ServiceTicketImpl(ServiceTicket.PREFIX + id, serviceTicketExpireInMills, service, this.id);
        if (onlyTrackMostRecentSession) {
            String path = normalizePath(service);
            Collection<String> existingServices = this.services.values();
            existingServices.stream().filter(existingService -> path.equals(normalizePath(existingService))).findFirst().ifPresent(existingServices::remove);
        }
        this.services.put(serviceTicket.getId(), service);
        return serviceTicket;
    }

    /**
     * Normalize the path of a service by removing the query string and everything after a semi-colon.
     *
     * @param service the service to normalize
     * @return the normalized path
     */
    private static String normalizePath(final String service) {
        String path = service;
        path = StringUtil.substringBefore(path, "?");
        path = StringUtil.substringBefore(path, ";");
        path = StringUtil.substringBefore(path, "#");
        return path;
    }
}
