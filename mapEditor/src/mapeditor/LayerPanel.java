package mapeditor;

import java.awt.Graphics;

import javax.swing.JPanel;

public class LayerPanel extends JPanel {
    // マップ
    public int[][] map;
    // マップの大きさ（単位：マス）
    private int row;
    private int col;
    // レイヤー番号
    private int layer_num;
    // マップチップパレット
    private PaletteDialog paletteDialog;
    private AutoTilePaletteDialog autoTileDialog;
    private static final int CHIP_SIZE = MainPanel.CHIP_SIZE;
   
    public LayerPanel(int lnum, int r, int c, PaletteDialog pt, AutoTilePaletteDialog at) {
        this.layer_num = lnum;
        this.paletteDialog = pt;
        this.autoTileDialog = at;
        this.setOpaque(false);
        // マップを初期化
        initMap(16, 16);
    }
    
    /**
     * マップを初期化する
     * @param row 行数
     * @param col 列数
     */
    public void initMap(int r, int c) {
        row = r;
        col = c;
        map = new int[r][c];

        // パレットで選択されているマップチップの番号を取得
        // int mapChipNo = paletteDialog.getSelectedMapChipNo();
        int mapChipNo = 2000;
        
        // そのマップチップでマップを埋めつくす
        for (int i = 0; i < r; i++) {
            for (int j = 0; j < c; j++) {
                map[i][j] = mapChipNo;
            }
        }
    }
   
    public void setIdOnMap(int y, int x) {
    	System.out.println();
        System.out.println("Current Layer:" + layer_num);
        System.out.println("Before map["+y+"]["+x+"] = " + map[y][x]);
        // パレットから取得した番号をセット
        if (x >= 0 && x < col && y >= 0 && y < row) {
        	if (paletteDialog.lastSelected){
        		map[y][x] = paletteDialog.getSelectedMapChipNo();
        	} else if (autoTileDialog.lastSelected) {
        		map[y][x] = autoTileDialog.getSelectedMapChipNo();
        	} else {
        		System.out.println("例外投げるべきだけどprintしちゃう！\n"
        				+ "マップチップ選択フラグ(paletteかauto tile paletetか)がおかしい！！");;
        	}
        	// 周辺のautoTileを更新
        	autoTileDialog.updateMapChipImage(y, x, layer_num, map);
        }
        System.out.println("After map["+y+"]["+x+"] = " + map[y][x]);

    }

    @Override
    public void paintComponent(Graphics g) {
        super.paintComponent(g);
        // map[][]に保存されているマップチップ番号をもとに画像を描画する
        for (int i = 0; i < row; i++) {
            for (int j = 0; j < col; j++) {
            	int chip_id = map[i][j];
            	if (autoTileDialog.isAutoTile(chip_id)) {
                    g.drawImage(autoTileDialog.getMapChipImage(i, j, layer_num), j * CHIP_SIZE, i * CHIP_SIZE, null);
            	}else {
            		g.drawImage(paletteDialog.getMapChipImage(chip_id), j * CHIP_SIZE, i * CHIP_SIZE, null);
            	}
            }
        }
    	
    }
}
