package mapeditor;

import java.awt.Color;
import java.awt.Dimension;
import java.awt.Graphics;
import java.awt.Image;
import java.awt.event.MouseAdapter;
import java.awt.event.MouseEvent;
import java.io.File;

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

    // マップチップのイメージ
    private Image mapChipImage;
    // マップチップをチップごとに分割したイメージ
    private Image[] mapChipImages;

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

        // マップチップイメージをロード
        loadImage();
    }

    /**
     * 選択されているマップチップ番号を返す
     * @return 選択されているマップチップ番号
     */
    public int getSelectedMapChipNo() {
        return selectedMapChipNo;
    }

    /**
     * 分割されたマップチップイメージを返す
     * @return 分割されたマップチップイメージ
     */
    public Image[] getMapChipImages() {
        return mapChipImages;
    }

    /**
     * マップチップイメージをロード
     */
    private void loadImage() {
    	String path = "image/auto_tile/";
    	int file_num = new File(path).list().length;
    	mapChipImage = createImage(WIDTH, HEIGHT);

        // 先頭のマップチップだけをmapChipImageに描画
        for (int i = 0; i < file_num; i++) {
        	int id = OFFSET_OF_ID + i;
            ImageIcon icon = new ImageIcon(path+id+".png");
            Image image = icon.getImage();

            // 書き込む
            int x = i % NUM_CHIPS_IN_ROW;
            int y = i / NUM_CHIPS_IN_ROW;
            Graphics g = mapChipImage.getGraphics();
            int dx1 = x * CHIP_SIZE;
            int dx2 = dx1 + CHIP_SIZE;
            int dy1 = y * CHIP_SIZE;
            int dy2 = dy1 + CHIP_SIZE;
            g.drawImage(image, dx1, dy1, dx2, dy2, 0, 0, CHIP_SIZE, CHIP_SIZE, null);
        }

    }

    // パレットパネル
    private class PalettePanel extends JPanel {
        public PalettePanel() {
            setPreferredSize(new Dimension(AutoTilePaletteDialog.WIDTH, AutoTilePaletteDialog.HEIGHT));

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
                    System.out.println("MainPanel map[y][x] = " + selectedMapChipNo);

                    // 選択されているマップチップを枠で囲む
                    repaint();
                }
            });
        }

        public void paintComponent(Graphics g) {
            g.setColor(new Color(32, 0, 0));
            g.fillRect(0, 0, AutoTilePaletteDialog.WIDTH, AutoTilePaletteDialog.HEIGHT);

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
