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

package org.disrupted.rumble.network.protocols.Rumble.packetformat;

import android.util.Log;

import org.disrupted.rumble.network.protocols.Rumble.packetformat.exceptions.MalformedRumblePacket;

import java.io.ByteArrayOutputStream;
import java.io.IOException;

/**
 * @author Marlinski
 */
public abstract class Block implements BlockBuilder, BlockMessage {

    private static final String TAG = "Block";

    protected BlockHeader header;
    protected byte[] payload;

    public Block(BlockHeader header, byte[] payload) {
        this.header = header;
        this.payload = payload;
    }

    @Override
    public byte[] getBytes() throws MalformedRumblePacket {
        try {
            ByteArrayOutputStream output;
            if (payload == null)
                output = new ByteArrayOutputStream(header.HEADER_LENGTH);
            else
                output = new ByteArrayOutputStream(header.HEADER_LENGTH + payload.length);

            byte[] headerBuffer = header.getBytes();
            if (headerBuffer == null)
                return null;
            output.write(headerBuffer);

            if (payload != null)
                output.write(payload);
            return output.toByteArray();
        } catch (IOException ignore) {
            throw new MalformedRumblePacket();
        }
    }

    public int getLength() {
        if(payload == null)
            return 0;
        return payload.length;
    }
}