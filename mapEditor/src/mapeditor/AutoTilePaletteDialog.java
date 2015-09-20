package mapeditor;

import java.awt.Color;
import java.awt.Dimension;
import java.awt.Graphics;
import java.awt.Image;
import java.awt.Point;
import java.awt.event.MouseAdapter;
import java.awt.event.MouseEvent;
import java.awt.image.BufferedImage;
import java.io.File;
import java.io.IOException;
import java.util.HashMap;

import javax.imageio.ImageIO;
import javax.swing.ImageIcon;
import javax.swing.JDialog;
import javax.swing.JFrame;
import javax.swing.JPanel;

public class AutoTilePaletteDialog extends JDialog {

	// パネルのサイズ（単位：ピクセル）
	public static final int WIDTH = 320;
	public static final int HEIGHT = 64;

	// マップチップのサイズ（単位：ピクセル）
	private static final int CHIP_SIZE = 32;

	// マップチップの数
	private static final int NUM_CHIPS = 20;
	private static final int NUM_CHIPS_IN_ROW = 10;

	// マップチップIDのオフセット
	private static final int OFFSET_OF_ID = 2000;

	// 最後に選択されてるかどうか（PaletteDialogとくらべて）
	public static boolean lastSelected;

	// マップチップのイメージ
	private Image mapChipImage;
	// マップチップをチップごとに分割したイメージ
	private Image[] mapChipImages;
	// mapと対応するautoTileを並べたhashmap
	private HashMap<Point, Image> imageMap;

	// 選択されているマップチップの番号
	private int selectedMapChipNo;

	public AutoTilePaletteDialog(JFrame owner) {
		// モードレスダイアログ
		super(owner, "オートタイルマップチップパレット", false);
		// 位置とサイズ
		setBounds(100, 90, WIDTH, HEIGHT);
		setResizable(false);

		// ダイアログにパネルを追加
		PalettePanel palettePanel = new PalettePanel();
		getContentPane().add(palettePanel);
		pack();

		imageMap = new HashMap<Point, Image>();
		// マップチップイメージをロード
		loadImage();
	}

	/**
	 * 選択されているマップチップ番号を返す
	 * 
	 * @return 選択されているマップチップ番号
	 */
	public int getSelectedMapChipNo() {
		return selectedMapChipNo + OFFSET_OF_ID;
	}

	/**
	 * 指定されたIDのマップチップイメージを返す
	 * 
	 * @return マップチップイメージ
	 */
	public Image getMapChipImage(int i, int j) {
		return imageMap.get(new Point(i, j));
	}
	
	public void updateMapChipImage(int i, int j, int[][] map) {
		// 画面端のオートタイルは閉じるタイブ、開くタイプにするならtrue
        boolean edge = true;
        int row = map.length;
        int col = map[0].length;
        int centeri = i;
        int centerj = j;
        for (i = centeri-1; i <= centeri+1; i++){
        	for (j = centerj-1; j <= centerj+1; j++){
        		// 注目してる map chip が範囲外, auto tileでない, ならスキップ
        		if (i < 0 || i >= row || j < 0 || j >= col){ continue; }
        		int chip_id = map[i][j];
        		if (chip_id < OFFSET_OF_ID){ continue; }
        		
        		boolean left_up = i == 0 ? edge : j == 0 ? edge : chip_id != map[i-1][j-1];
        		boolean left = j == 0 ? edge : chip_id != map[i][j-1];
        		boolean left_down = i == row - 1 ? edge : j == 0 ? edge : chip_id != map[i+1][j-1];
        		boolean right_up =  i == 0 ? edge : j == col - 1 ? edge : chip_id != map[i-1][j+1];
        		boolean right =  j == col - 1 ? edge : chip_id != map[i][j+1];
        		boolean right_down =  i == row - 1 ? edge : j == col - 1 ? edge : chip_id != map[i+1][j+1];
        		boolean up =  i == 0 ? edge : chip_id != map[i-1][j];
        		boolean down =  i == row - 1 ? edge : chip_id != map[i+1][j];
        		boolean[][] around_info = {
        			{left_up, up, right_up}, 
        			{left, false, right},
        			{left_down, down, right_down} 
        		};
        		Image wholeImage = mapChipImages[chip_id - OFFSET_OF_ID];
    			BufferedImage bwholeImage = new BufferedImage(wholeImage.getWidth(null), wholeImage.getHeight(null), BufferedImage.TYPE_INT_ARGB_PRE);
    			bwholeImage.createGraphics().drawImage(wholeImage, 0, 0, null);
    			BufferedImage autoTile = new BufferedImage(wholeImage.getWidth(null), wholeImage.getHeight(null) / 5, BufferedImage.TYPE_INT_ARGB_PRE);
        		BufferedImage drawAutoTileImage = AutoTileUtil.getAutoTileImage(bwholeImage, autoTile,
        				AutoTileUtil.aroudTileArrayToDrawTileArray(around_info));
        		imageMap.put(new Point(i, j), drawAutoTileImage);
        	}
        }
	}
	
	/**
	 * マップチップイメージをロード
	 */
	private void loadImage() {
		String path = "image/auto_tile/";
		int file_num = new File(path).list().length;
		mapChipImage = createImage(WIDTH, HEIGHT);
		mapChipImages = new Image[NUM_CHIPS];

		// 先頭のマップチップだけをmapChipImageに描画
		for (int i = 0; i < file_num; i++) {
			int id = OFFSET_OF_ID + i;
			BufferedImage image = null;
			try {
				image = ImageIO.read(new File(path + id + ".png"));
			} catch (IOException e) {
				// TODO 自動生成された catch ブロック
				e.printStackTrace();
			}
			// ここでauto tile全体のimageを入れておく
			// 後で周囲の情報をもとに抜き出す
			mapChipImages[i] = image;

			// 書き込む
			int x = i % NUM_CHIPS_IN_ROW;
			int y = i / NUM_CHIPS_IN_ROW;
			Graphics g = mapChipImage.getGraphics();
			int dx1 = x * CHIP_SIZE;
			int dx2 = dx1 + CHIP_SIZE;
			int dy1 = y * CHIP_SIZE;
			int dy2 = dy1 + CHIP_SIZE;
			g.drawImage(image, dx1, dy1, dx2, dy2, 0, 0, CHIP_SIZE, CHIP_SIZE,
					null);
		}

	}

	public boolean isAutoTile(int id) {
		return id >= OFFSET_OF_ID;
	}

	// パレットパネル
	private class PalettePanel extends JPanel {
		public PalettePanel() {
			setPreferredSize(new Dimension(AutoTilePaletteDialog.WIDTH,
					AutoTilePaletteDialog.HEIGHT));

			// マウスでクリックしたときそのマップチップを選択する
			addMouseListener(new MouseAdapter() {
				public void mouseClicked(MouseEvent e) {
					int x = e.getX() / CHIP_SIZE;
					int y = e.getY() / CHIP_SIZE;

					// マップチップ番号は左上から0,1,2と数える
					int mapChipNo = y * NUM_CHIPS_IN_ROW + x;
					if (mapChipNo > NUM_CHIPS) {
						mapChipNo = NUM_CHIPS;
					}

					selectedMapChipNo = mapChipNo;
					System.out.println("select map chip (auto tile): "
							+ selectedMapChipNo);

					// 選択フラグの更新
					lastSelected = true;
					PaletteDialog.lastSelected = false;

					// 選択されているマップチップを枠で囲む
					repaint();
				}
			});
		}

		public void paintComponent(Graphics g) {
			g.setColor(new Color(32, 0, 0));
			g.fillRect(0, 0, AutoTilePaletteDialog.WIDTH,
					AutoTilePaletteDialog.HEIGHT);

			// マップチップイメージを描画
			g.drawImage(mapChipImage, 0, 0, this);

			// 選択されているマップチップを枠で囲む
			int x = selectedMapChipNo % NUM_CHIPS_IN_ROW;
			int y = selectedMapChipNo / NUM_CHIPS_IN_ROW;
			g.setColor(Color.RED);
			g.drawRect(x * CHIP_SIZE, y * CHIP_SIZE, CHIP_SIZE, CHIP_SIZE);
		}
	}
}
