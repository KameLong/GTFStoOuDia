package com.kamelong.tool;

public class Font {
    public static final Font OUDIA_DEFAULT=new Font("ＭＳ ゴシック",9,false,false);
    /**
     * フォント高さ
     */
    public int height=-1;
    /**
     * フォント名
     */
    public String name=null;
    /**
     * 太字なら１
     */
    public boolean bold=false;
    /**
     * 斜体なら１
     */
    public boolean itaric=false;

    private static final String HEIGHT="height";
    private static final String NAME="facename";
    private static final String BOLD="bold";
    private static final String ITARIC="itaric";

    public Font(){

    }
    private Font(String name, int height, boolean bold, boolean itaric){
        this.name=name;
        this.height=height;
        this.bold=bold;
        this.itaric=itaric;
    }

    /**
     * フォントをOuDia形式のテキストとして出力する
     * @return
     */
    public StringBuilder font2OudiaFontTxt(){
        StringBuilder result=new StringBuilder();
        result.append("PointTextHeight=").append(height);
        if(name!=null){
            result.append(";Facename=").append(name);
        }else{
            result.append(";Facename=").append("ＭＳ ゴシック");
        }
        if(bold){
            result.append(";Bold=1");
        }
        if(itaric){
            result.append("Itaric=1");
        }
        return result;
    }


}
