package com.cobnet.connection.support;

import com.cobnet.common.Endian;
import com.cobnet.interfaces.connection.Encodable;
import com.cobnet.spring.boot.controller.handler.OutboundOperation;
import com.cobnet.spring.boot.core.ProjectBeanHolder;
import com.fasterxml.jackson.core.JsonProcessingException;
import io.netty.buffer.ByteBuf;
import io.netty.buffer.ByteBufAllocator;
import io.netty.buffer.Unpooled;

import java.io.Serializable;
import java.util.Arrays;
import java.util.Map;

public class OutboundPacket extends Packet {

	private long opcode;
	
	private final ByteBuf byteBuf;
	
	public OutboundPacket(OutboundOperation operation, NettyServer<?> server) {

		this(operation.code(), server);
	}
	
    public OutboundPacket(long opcode, NettyServer<?> server) {

        this(new byte[]{}, server);

        encodeUInt(opcode);

        this.opcode = opcode;
    }

	public OutboundPacket(byte[] data, NettyServer<?> server) {

		super(data, server);

        this.byteBuf = ByteBufAllocator.DEFAULT.buffer().clear();

        encode(data);

        ByteBuf buf = Unpooled.buffer();

        buf.writeBytes(Arrays.copyOfRange(data, 0, 4));

        if(this.getServer().getEndian() == Endian.BIG) {

        	this.opcode = buf.readUnsignedInt();

        } else {

        	this.opcode = buf.readUnsignedIntLE();
        }

        buf.release();
	}
    
    public OutboundOperation getOperation() {

        return OutboundOperation.getByCode(opcode);
    }
    
    public long getOpcode() {

        return this.opcode;
    }
    
    public void encode(byte[] data) {

        byteBuf.writeBytes(data);
    }
    
    public void encode(Encodable encodable) {

        encodable.encode(this);
    }
    
    public void encodeByte(byte b) {

        byteBuf.writeByte(b);
    }
    
    public void encodeByte(int b) {

        encodeByte((byte)b);
    }
    
    public void encodeBoolean(boolean b) {

        byteBuf.writeBoolean(b);
    }
    
    public void encodeShort(short value) {

    	if(this.getServer().getEndian() == Endian.BIG) {

    		byteBuf.writeShort(value);

    	} else {

    		byteBuf.writeShortLE(value);
    	}
    }
    
    public void encodeUShort(int value) {
		encodeShort((short)(value & 0xFFFF));
    }
    
    public void encodeInt(int value) {

    	if(this.getServer().getEndian() == Endian.BIG) {

    		byteBuf.writeInt(value);

    	} else {

    		byteBuf.writeIntLE(value);
    	}
    }
    
    public void encodeUInt(long value) {

        encodeInt((int)(value & 0xFFFFFFFFL));
    }
    
    public void encodeLong(long value) {

    	if(this.getServer().getEndian() == Endian.BIG) {

    		byteBuf.writeLong(value);

    	} else {

    		byteBuf.writeLongLE(value);
    	}
    }
    
    public void encodeString(String value) {

    	byte[] bs = value.getBytes(getServer().getCharset());

        encodeUInt(bs.length);

        encode(bs);
    }

	public <K extends Serializable, V extends Serializable> void encodeMap(Map<K, V> map) throws JsonProcessingException {

    	encodeString(ProjectBeanHolder.getObjectMapper().writeValueAsString(map));
    }

    @Override
    public void setData(byte[] data) {

        super.setData(data);

        byteBuf.clear();

        encode(data);
    }

    @Override
    public byte[] getData() {

        if (byteBuf.hasArray()) {

            return byteBuf.array();

        } else {

            byte[] arr = new byte[byteBuf.writerIndex()];

            byteBuf.nioBuffer().get(arr, 0, byteBuf.writerIndex());

            return arr;
        }
    }
    
	@Override
	public ByteBuf getBuf() {

        return byteBuf.asReadOnly();
	}
	
	@Override
	public void dispose() {

        byteBuf.clear();
	}

	@Override
	public Packet clone() throws CloneNotSupportedException {

        return new OutboundPacket(this.getData(), this.getServer());
	}
	
    @Override
    protected void finalize() throws Throwable {

    	dispose();
    }


}
