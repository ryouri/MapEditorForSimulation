package mapeditor;

import java.awt.Graphics;
import java.awt.Image;
import java.awt.Point;

public class AutoTileUtil {

	/**
	 * 3 * 3のタイルの配置情報を，2 * 2のポイントのオートタイル描画情報を返す
	 * @param aroundTileArray 3 * 3のタイルの配置情報が格納されている配列, 塞ぐマップチップにするかどうか
	 * @return 2 * 2 のPointの配列，オートタイルの描画情報が格納されている
	 */
	public static Point[][] aroudTileArrayToDrawTileArray(boolean[][] aroundTileArray) {
		boolean left = aroundTileArray[1][0];
		boolean right = aroundTileArray[1][2];
		boolean up = aroundTileArray[0][1];
		boolean down = aroundTileArray[2][1];
		boolean left_up = aroundTileArray[0][0];
		boolean left_down = aroundTileArray[2][0];
		boolean right_up = aroundTileArray[0][2];
		boolean right_down = aroundTileArray[2][2];
		
		Point result[][] = new Point[2][2];
		// 左上 [0][0]
		int id = getTileID(left, up, left_up);
		result[0][0] = new Point(0, id * 2);

		// 右上 [0][1]
		id = getTileID(right, up, right_up);
		result[0][1] = new Point(1, id * 2);

		// 左下 [1][0]
		id = getTileID(left, down, left_down);
		result[1][0] = new Point(0, id * 2 + 1);

		// 右下 [1][1]
		id = getTileID(right, down, right_down);
		result[1][1] = new Point(1, id * 2 + 1);

		return result;
	}

	/**
	 * 縦, 横, 斜めの情報からTileIDを返す
	 * 返すIDは(オートタイルのマップチップの)上から順番に0, 1, 2, 3, 4
	 * @param horizontal 横のつながりがあるか
	 * @param vertical 縦のつながりがあるか
	 * @param diagonal 斜めのつながりがあるか
	 * @return タイルのID
	 */
	private static int getTileID(boolean horizontal, boolean vertical, boolean diagonal) {
		if (vertical && horizontal) {
			return 0;
		}
		else if (horizontal) {
			return 1;
		}
		else if (vertical) {
			return 2;
		}
		else if (diagonal) {
			return 3;
		}
		else {
			return 4;
		}
	}
	
	public static Image getAutoTileImage(Image wholeImage, Image autoTileImage, Point[][] drawInfo) {
		int half_size = autoTileImage.getWidth(null) / 2;
	
		Graphics g = autoTileImage.getGraphics();
		for (int i = 0; i < 2; i++) {
			for (int j = 0; j < 2; j++) {
				int dx1 = j * half_size; int dx2 = dx1 + half_size;
				int dy1 = i * half_size; int dy2 = dy1 + half_size;
				int sx1 = drawInfo[i][j].x * half_size; int sx2 = sx1 + half_size;
				int sy1 = drawInfo[i][j].y * half_size; int sy2 = sy1 + half_size;
				
				g.drawImage(wholeImage, dx1, dy1, dx2, dy2, sx1, sy1, sx2, sy2, null);
			}
		}
		return autoTileImage;
	}
}
