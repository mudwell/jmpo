package jp.skr.soundwing.mpo;

import static jp.skr.soundwing.exif.ExifUtil.startsWith;

/**
 * MP個別情報IFD.
 * 
 * @author mudwell
 * 
 */
public class MpAttributeFields {
	static final byte[] VERSION_TAG = { (byte) 0xb0, 0x00 };
	static final byte[] MP_INDIVIDUAL_NUM_TAG = { (byte) 0xb1, 0x01 };
	static final byte[] BASE_VIEWPOINT_NUM_TAG = { (byte) 0xb2, 0x04 };
	static final byte[] CONVERGENCE_ANGLE_TAG = { (byte) 0xb2, 0x05 };
	static final byte[] BASELINE_LENGTH_TAG = { (byte) 0xb2, 0x06 };

	static final int COUNT_LENGTH = 2;
	static final int VERSION_LENGTH = 12;
	static final int MP_INDIVIDUAL_NUM_LENGTH = 12;
	static final int BASE_VIEWPOINT_NUM_LENGTH = 12;
	static final int CONVERGENCE_ANGLE_LENGTH = 12;
	static final int BASELINE_LENGTH_LENGTH = 12;
	static final int OFFSET_OF_NEXT_IFD_LENGTH = 4;

	byte[] count;
	byte[] version;
	byte[] mpIndividualNum;
	byte[] baseViewpointNum;
	private Rational convergenceAngle;
	private Rational baselineLength;
	byte[] offsetOfNextIFD;

	public MpAttributeFields() {
		count = new byte[COUNT_LENGTH];
		offsetOfNextIFD = new byte[OFFSET_OF_NEXT_IFD_LENGTH];
	}

	/**
	 * @param fileData
	 * @param attrHead
	 * @param offsetBase
	 * @return
	 */
	public static MpAttributeFields create(byte[] fileData, int attrHead,
			int offsetBase) {
		MpAttributeFields mpf = new MpAttributeFields();
		int pos = attrHead;
		System.out.printf("MPAHEAD: %x\n", attrHead);
		System.arraycopy(fileData, attrHead, mpf.count, 0, mpf.count.length);
		int count = MpoLoader.getShort(fileData, attrHead);
		System.out.println("count: " + count);

		for (int i = 0; i < count; i++) {
			int pTag = attrHead + 2 + 12 * i;
			if (startsWith(fileData, pTag, VERSION_TAG)) {
				mpf.version = new byte[VERSION_LENGTH];
				System.arraycopy(fileData, pTag, mpf.version, 0,
						mpf.version.length);
			} else if (startsWith(fileData, pTag, MP_INDIVIDUAL_NUM_TAG)) {
				mpf.mpIndividualNum = new byte[MP_INDIVIDUAL_NUM_LENGTH];
				System.arraycopy(fileData, pTag, mpf.mpIndividualNum, 0,
						mpf.mpIndividualNum.length);
			} else if (startsWith(fileData, pTag, BASE_VIEWPOINT_NUM_TAG)) {
				mpf.baseViewpointNum = new byte[BASE_VIEWPOINT_NUM_LENGTH];
				System.arraycopy(fileData, pTag, mpf.baseViewpointNum, 0,
						mpf.baseViewpointNum.length);
			} else if (startsWith(fileData, pTag, CONVERGENCE_ANGLE_TAG)) {
				int offset = MpoLoader.getInt(fileData, pTag + 8);
				System.out.printf("ConvergenceAngleOffset: %x\n", offset
						+ offsetBase);
				int n = MpoLoader.getInt(fileData, offset + offsetBase);
				int d = MpoLoader.getInt(fileData, offset + offsetBase + 4);
				mpf.convergenceAngle = new Rational(n, d);
			} else if (startsWith(fileData, pTag, BASELINE_LENGTH_TAG)) {
				int offset = MpoLoader.getInt(fileData, pTag + 8);
				int n = MpoLoader.getInt(fileData, offset + offsetBase);
				int d = MpoLoader.getInt(fileData, offset + offsetBase + 4);
				mpf.baselineLength = new Rational(n, d);
			}
		}

		System.arraycopy(fileData, attrHead + 2 + 12 * count,
				mpf.offsetOfNextIFD, 0, mpf.offsetOfNextIFD.length);

		return mpf;
	}

	/**
	 * 個別画像番号を取得する.
	 * 
	 * 個別画像番号が存在していなかった場合は-1を返す.
	 * 
	 * @return 個別画像番号(存在しない場合は-1)
	 */
	public int getMPIndividualNum() {
		if (mpIndividualNum != null) {
			return MpoLoader.getInt(mpIndividualNum, mpIndividualNum.length
					- MpIndexFields.INTEGER_SIZE);
		} else {
			return -1;
		}
	}

	/**
	 * 輻輳角を返す.
	 * 
	 * @return 輻輳角(-180〜180)[degree]
	 */
	public Rational getConvergenceAngle() {
		return convergenceAngle;
	}

	/**
	 * 基線長を返す.
	 * 
	 * @return 基線長[m]
	 */
	public Rational getBaselineLength() {
		return baselineLength;
	}

	public void setConvergenceAngle(Rational rational) {
		this.convergenceAngle = rational;

	}

	public int getOffsetOfNextIFD() {
		return MpoLoader.getInt(offsetOfNextIFD, offsetOfNextIFD.length - 4);
	}
}