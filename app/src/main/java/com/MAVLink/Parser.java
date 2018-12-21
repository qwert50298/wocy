/* AUTO-GENERATED FILE.  DO NOT MODIFY.
 *
 * This class was automatically generated by the
 * java mavlink generator tool. It should not be modified by hand.
 */

package com.MAVLink;

import com.MAVLink.MAVLinkPacket;
import com.MAVLink.Messages.MAVLinkStats;

public class Parser {

    /**
     * States from the parsing state machine
     */
    enum MAV_states {
        MAVLINK_PARSE_STATE_UNINIT, MAVLINK_PARSE_STATE_IDLE, MAVLINK_PARSE_STATE_GOT_STX, MAVLINK_PARSE_STATE_GOT_LENGTH, MAVLINK_PARSE_STATE_GOT_SEQ, MAVLINK_PARSE_STATE_GOT_SYSID, MAVLINK_PARSE_STATE_GOT_COMPID, MAVLINK_PARSE_STATE_GOT_MSGID, MAVLINK_PARSE_STATE_GOT_CRC1, MAVLINK_PARSE_STATE_GOT_PAYLOAD
    }

    MAV_states state = MAV_states.MAVLINK_PARSE_STATE_UNINIT;

    public MAVLinkStats stats;
    private MAVLinkPacket m;

    public Parser() {
        this(false);
    }

    public Parser(boolean ignoreRadioPacketStats) {
        stats = new MAVLinkStats(ignoreRadioPacketStats);
    }

    /**
     * This is a convenience function which handles the complete MAVLink
     * parsing. the function will parse one byte at a time and return the
     * complete packet once it could be successfully decoded. Checksum and other
     * failures will be silently ignored.
     * 
     * @param c
     *            The char to parse
     */
    public MAVLinkPacket mavlink_parse_char(int c) {

        switch (state) {
        case MAVLINK_PARSE_STATE_UNINIT:
        case MAVLINK_PARSE_STATE_IDLE:

            if (c == MAVLinkPacket.MAVLINK_STX) {
                state = MAV_states.MAVLINK_PARSE_STATE_GOT_STX;
            }
            break;

        case MAVLINK_PARSE_STATE_GOT_STX:
            m = new MAVLinkPacket(c);
            state = MAV_states.MAVLINK_PARSE_STATE_GOT_LENGTH;
            break;

        case MAVLINK_PARSE_STATE_GOT_LENGTH:
            m.seq = c;
            state = MAV_states.MAVLINK_PARSE_STATE_GOT_SEQ;
            break;

        case MAVLINK_PARSE_STATE_GOT_SEQ:
            m.sysid = c;
            state = MAV_states.MAVLINK_PARSE_STATE_GOT_SYSID;
            break;

        case MAVLINK_PARSE_STATE_GOT_SYSID:
            m.compid = c;
            state = MAV_states.MAVLINK_PARSE_STATE_GOT_COMPID;
            break;

        case MAVLINK_PARSE_STATE_GOT_COMPID:
            m.msgid = c;
            if (m.len == 0) {
                state = MAV_states.MAVLINK_PARSE_STATE_GOT_PAYLOAD;
            } else {
                state = MAV_states.MAVLINK_PARSE_STATE_GOT_MSGID;
            }
            break;

        case MAVLINK_PARSE_STATE_GOT_MSGID:
            m.payload.add((byte) c);
            if (m.payloadIsFilled()) {
                state = MAV_states.MAVLINK_PARSE_STATE_GOT_PAYLOAD;
            }
            break;

        case MAVLINK_PARSE_STATE_GOT_PAYLOAD:
            m.generateCRC();
            // Check first checksum byte
            if (c != m.crc.getLSB()) {
                state = MAV_states.MAVLINK_PARSE_STATE_IDLE;
                if (c == MAVLinkPacket.MAVLINK_STX) {
                    state = MAV_states.MAVLINK_PARSE_STATE_GOT_STX;
                    m.crc.start_checksum();
                }
                stats.crcError();
            } else {
                state = MAV_states.MAVLINK_PARSE_STATE_GOT_CRC1;
            }
            break;

        case MAVLINK_PARSE_STATE_GOT_CRC1:
            // Check second checksum byte
            if (c != m.crc.getMSB()) {
                state = MAV_states.MAVLINK_PARSE_STATE_IDLE;
                if (c == MAVLinkPacket.MAVLINK_STX) {
                    state = MAV_states.MAVLINK_PARSE_STATE_GOT_STX;
                    m.crc.start_checksum();
                }
                stats.crcError();
            } else { // Successfully received the message
                stats.newPacket(m);
                state = MAV_states.MAVLINK_PARSE_STATE_IDLE;
                return m;
            }
            break;
        }
        return null;
    }
}
