package jp.skr.soundwing.mpo;

/**
 * MPエントリー.
 * 
 * @author mudwell
 * 
 */
public class MpEntry {
	private static final int TYPE_MASK = 0x0fff;
	/**
	 * 個別画像種別管理情報.
	 */
	private int info;
	/**
	 * 個別画像サイズ.
	 */
	private int size;
	/**
	 * 個別画像データオフセット.
	 */
	private int offset;
	/**
	 * 従属画像のエントリ番号.
	 */
	private int image1EntryNumber;
	private int image2EntryNumber;

	/**
	 * @param buffer
	 * @param mpentryHead
	 */
	public MpEntry(byte[] buffer, int mpentryHead) {
		System.out.printf("MPEntry Head: %x\n", mpentryHead);
		this.info = MpoLoader.getInt(buffer, mpentryHead);
		this.size = MpoLoader.getInt(buffer, mpentryHead + 4);
		this.offset = MpoLoader.getInt(buffer, mpentryHead + 8);
	}

	/**
	 * @return
	 */
	public int getSize() {
		return size;
	}

	/**
	 * 個別画像へのデータオフセットを返す.
	 * 
	 * 先頭画像の場合は0を返す.
	 * 
	 * @return 個別画像へのデータオフセット(先頭画像の場合は0)
	 */
	public int getOffset() {
		return offset;
	}

	/**
	 * 種別コードを返す.
	 * 
	 * @return 種別コード
	 */
	public int getTypeCode() {
		return info & TYPE_MASK;
	}
}