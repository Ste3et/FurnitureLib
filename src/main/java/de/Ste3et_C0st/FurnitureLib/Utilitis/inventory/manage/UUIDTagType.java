package de.Ste3et_C0st.FurnitureLib.Utilitis.inventory.manage;

import org.bukkit.persistence.PersistentDataAdapterContext;
import org.bukkit.persistence.PersistentDataType;
import java.nio.ByteBuffer;
import java.util.UUID;

/**
 * A {@link PersistentDataType} implementation that adds support for {@link UUID}s.
 *
 * @since 0.6.0
 */
public final class UUIDTagType implements PersistentDataType<byte[], UUID> {
	
	/**
	 * The one and only instance of this class.
	 * Since this class stores no state information (apart from this field),
	 * the usage of a single instance is safe even across multiple threads.
	 */
	public static final UUIDTagType INSTANCE = new UUIDTagType();
	
	/**
	 * A private constructor so that only a single instance of this class can exist.
	 */
	private UUIDTagType() {}
	
	@Override
	public Class<byte[]> getPrimitiveType() {
		return byte[].class;
	}
	
	@Override
	public Class<UUID> getComplexType() {
		return UUID.class;
	}
	
	@Override
	public byte[] toPrimitive(UUID complex, PersistentDataAdapterContext context) {
		ByteBuffer buffer = ByteBuffer.wrap(new byte[16]);
		buffer.putLong(complex.getMostSignificantBits());
		buffer.putLong(complex.getLeastSignificantBits());
		return buffer.array();
	}
	
	@Override
	public UUID fromPrimitive(byte[] primitive, PersistentDataAdapterContext context) {
		ByteBuffer buffer = ByteBuffer.wrap(primitive);
		long most = buffer.getLong();
		long least = buffer.getLong();
		return new UUID(most, least);
	}
}