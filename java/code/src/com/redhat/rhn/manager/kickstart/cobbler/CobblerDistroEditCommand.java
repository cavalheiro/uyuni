/**
 * Copyright (c) 2009--2014 Red Hat, Inc.
 *
 * This software is licensed to you under the GNU General Public License,
 * version 2 (GPLv2). There is NO WARRANTY for this software, express or
 * implied, including the implied warranties of MERCHANTABILITY or FITNESS
 * FOR A PARTICULAR PURPOSE. You should have received a copy of GPLv2
 * along with this software; if not, see
 * http://www.gnu.org/licenses/old-licenses/gpl-2.0.txt.
 *
 * Red Hat trademarks are not licensed under GPLv2. No permission is
 * granted to use or replicate Red Hat trademarks that are incorporated
 * in this software or its documentation.
 */
package com.redhat.rhn.manager.kickstart.cobbler;

import com.redhat.rhn.common.validator.ValidatorError;
import com.redhat.rhn.domain.kickstart.KickstartableTree;
import com.redhat.rhn.domain.user.User;
import org.cobbler.CobblerConnection;
import org.cobbler.Distro;

import java.util.Map;

/**
 * KickstartCobblerCommand - class to contain logic to communicate with cobbler
 * @version $Rev$
 */
public class CobblerDistroEditCommand extends CobblerDistroCommand {

    /**
     * Constructor
     * @param ksTreeIn to sync
     * @param userIn - user wanting to sync with cobbler
     */
    public CobblerDistroEditCommand(KickstartableTree ksTreeIn,
            User userIn) {
        super(ksTreeIn, userIn);
    }

    /**
     * Constructor - for use with taskomatic
     * @param ksTreeIn to sync
     */
    public CobblerDistroEditCommand(KickstartableTree ksTreeIn) {
        super(ksTreeIn);
    }

    /**
     * {@inheritDoc}
     */
    @Override
    public ValidatorError store() {
        CobblerConnection con = getCobblerConnection();
        Distro nonXen = Distro.lookupById(con, tree.getCobblerId());
        Distro xen = Distro.lookupById(con, tree.getCobblerXenId());

        Map<String, String> ksmeta = createKsMetadataFromTree(this.tree);

        // set architecture (fix 32bit vm's on a 64bit system)
        // especially for SUSE where the kernel+initrd is under a path that contains
        // the $arch
        String archName = tree.getChannel().getChannelArch().getName();
        if (archName.equals("IA-32")) {
            archName = "i386";
        }

        //if the newly edited tree does para virt....
        if (tree.doesParaVirt()) {
            //IT does paravirt so we need to either update the xen distro or create one
            if (xen == null) {
                xen = Distro.create(con, tree.getCobblerXenDistroName(), tree
                        .getKernelXenPath(), tree.getInitrdXenPath(), ksmeta, tree
                        .getInstallType().getCobblerBreed(), tree.getInstallType()
                        .getCobblerOsVersion(), tree.getChannel().getChannelArch()
                        .cobblerArch());
                tree.setCobblerXenId(xen.getId());
            }
            else {
                xen.setArch(archName);
                xen.setKernel(tree.getKernelXenPath());
                xen.setInitrd(tree.getInitrdXenPath());
                xen.setBreed(tree.getInstallType().getCobblerBreed());
                xen.setOsVersion(tree.getInstallType().getCobblerOsVersion());
                xen.setKsMeta(ksmeta);
                xen.setBreed(tree.getInstallType().getCobblerBreed());
                xen.setArch(tree.getChannel().getChannelArch().cobblerArch());
                xen.save();
            }
        }
        else {
            //it doesn't do paravirt, so we need to delete the xen distro
            if (xen != null) {
                xen.remove();
                tree.setCobblerXenId(null);
            }
        }

        if (nonXen != null) {
            nonXen.setArch(archName);
            nonXen.setInitrd(tree.getInitrdPath());
            nonXen.setKernel(tree.getKernelPath());
            nonXen.setBreed(tree.getInstallType().getCobblerBreed());
            nonXen.setOsVersion(tree.getInstallType().getCobblerOsVersion());
            nonXen.setKsMeta(ksmeta);
            nonXen.setArch(tree.getChannel().getChannelArch().cobblerArch());
            nonXen.save();
        }
        return null;
    }

}
