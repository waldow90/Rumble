/*
 * Copyright (C) 2014 Disrupted Systems
 *
 * This file is part of Rumble.
 *
 * Rumble is free software: you can redistribute it and/or modify
 * it under the terms of the GNU General Public License as published by
 * the Free Software Foundation, either version 3 of the License, or
 * (at your option) any later version.
 *
 * Rumble is distributed in the hope that it will be useful,
 * but WITHOUT ANY WARRANTY; without even the implied warranty of
 * MERCHANTABILITY or FITNESS FOR A PARTICULAR PURPOSE.  See the
 * GNU General Public License for more details.
 *
 * You should have received a copy of the GNU General Public License
 * along with Rumble.  If not, see <http://www.gnu.org/licenses/>.
 */

package org.disrupted.rumble.network;

import android.util.Log;

import org.disrupted.rumble.network.events.ConnectToNeighbourDevice;
import org.disrupted.rumble.network.events.DisconnectFromNeighbourDevice;
import org.disrupted.rumble.network.protocols.Protocol;

import java.util.HashSet;
import java.util.LinkedHashSet;

import de.greenrobot.event.EventBus;

/**
 * @author Marlinski
 */
public class NeighbourDevice {
    private static final String TAG = "NeighbourDevice";

    protected String deviceName;
    protected String type;
    protected String macAddress;
    protected DeviceStatus statusFlag;
    protected HashSet<Protocol> protocolSet;

    private boolean protocolRumble  = false;
    private boolean protocolFirechat = false;
    private boolean protocolUnknown  =  true;

    public NeighbourDevice(NeighbourDevice nearby){
        this.type = nearby.getType();
        this.deviceName  = nearby.deviceName;
        this.macAddress  = nearby.getMacAddress();
        this.statusFlag  = nearby.statusFlag;
        this.protocolSet = nearby.protocolSet;
        this.protocolFirechat = isFirechatter();
        this.protocolRumble   = isRumbler();
        this.protocolUnknown  = isProtocolUnknown();

    }

    public NeighbourDevice(String macAddress, String type){
        this.type = type;
        this.macAddress  = macAddress;
        this.statusFlag = new DeviceStatus();
        this.protocolSet = new LinkedHashSet<Protocol>();
        this.protocolFirechat = false;
        this.protocolRumble  = false;
        this.protocolUnknown  = true;
    }

    public String   getDeviceName() { return deviceName;      }
    public String   getType() {       return type;            }
    public String   getMacAddress() { return this.macAddress; }
    public Protocol getProtocol(String protocolID) {
        for(Protocol protocol : protocolSet) {
            if(protocol.getProtocolID().equals(protocolID))
                return protocol;
        }
        return null;
    }


    public void setDeviceName(String deviceName) {  this.deviceName = deviceName;   }
    public void setFirechatFlag() {
        protocolFirechat = true;
        protocolUnknown = false;
    }
    public void setRumbleFlag() {
        protocolRumble = true;
        protocolUnknown = false;
    }
    private void resetFlags() {
        protocolRumble   = false;
        protocolFirechat = false;
        protocolUnknown  = true;
    }
    private void setConnected() {
        statusFlag.flags |= DeviceStatus.CONNECTED;
        statusFlag.flags &= ~DeviceStatus.DISCONNECTED;
        EventBus.getDefault().post(new ConnectToNeighbourDevice());
    }
    private void unsetConnected() {
        statusFlag.flags |= DeviceStatus.DISCONNECTED;
        statusFlag.flags &= ~DeviceStatus.CONNECTED;
        EventBus.getDefault().post(new DisconnectFromNeighbourDevice());
    }
    public void addProtocol(Protocol newProtocol) {
        for(Protocol protocol : protocolSet) {
            if(protocol.getProtocolID().equals(protocol.getProtocolID())) {
                Log.d(TAG, "[!] this device is already connected with this protocol !");
                protocolSet.remove(protocol);
            }
        }
        protocolSet.add(newProtocol);
        this.setConnected();
    }
    public boolean delProtocol(String protocolID) {
        for(Protocol protocol : protocolSet) {
            if(protocol.getProtocolID().equals(protocolID)) {
                Log.d(TAG, "[-] remove protocol "+protocolID+" associated with device "+macAddress);
                protocolSet.remove(protocol);
                if(protocolSet.size() == 0)
                    this.unsetConnected();
                return true;
            }
        }
        Log.d(TAG, "[!] this device is not connected with this protocol");
        return false;
    }



    public boolean isRumbler() {
        return protocolRumble;
    }
    public boolean isFirechatter() {      return protocolFirechat; }
    public boolean isProtocolUnknown() {  return protocolUnknown;  }
    public boolean isConnected() {
        return ((statusFlag.flags & DeviceStatus.CONNECTED) > 0);
    }
    public boolean isConnected(String protocolID) {
        for(Protocol protocol : protocolSet) {
            if(protocol.getProtocolID().equals(protocolID))
                return true;
        }
        return false;
    }


    @Override
    public String toString() {
        return "MAC address: " + macAddress;
    }
    @Override
    public int hashCode() {
        return macAddress.hashCode();
    }

    @Override
    public boolean equals(Object obj) {
        if (!(obj instanceof NeighbourDevice))
            return false;
        if (obj == this)
            return true;

        NeighbourDevice device = (NeighbourDevice) obj;
        return device.getMacAddress().equals(this.macAddress);
    }

    private class DeviceStatus {
        public static final int CONNECTED    = (1 << 0);
        public static final int DISCONNECTED = (1 << 1);

        public int flags;

        public DeviceStatus() {
            flags = DISCONNECTED;
        }
    }

}