/*
 * To change this license header, choose License Headers in Project Properties.
 * To change this template file, choose Tools | Templates
 * and open the template in the editor.
 */
package org.neuromorpho.literature.portal.repository;

import java.util.Date;

public interface PortalLogExtendedRepository {

    public void updateLog(String portalName, Date launchedDate, Integer total);
}
