package utils.VColor;

import java.awt.*;

public class VColor {
    private String hexColor;
    private String vGene;

    public VColor(String vGene) {
        this.vGene = vGene;
        this.hexColor = findColor(vGene);
    }

    private String findColor(String vGene) {
        String fPart = vGene.contains("-") ? vGene.substring(0, vGene.indexOf("-")) : vGene;
        String hexColor;
        switch (fPart) {
            case "TRBV1":
                hexColor = "2ca25f";
                break;
            case "TRBV2":
                hexColor = "99d8c9";
                break;
            case "TRBV3":
                hexColor = "8856a7";
                break;
            case "TRBV4":
                hexColor = "9ebcda";
                break;
            case "TRBV5":
                hexColor = "a1d99b";
                break;
            case "TRBV6":
                hexColor = "e34a33";
                break;
            case "TRBV7":
                hexColor = "fdbb84";
                break;
            case "TRBV8":
                hexColor = "c994c7";
                break;
            case "TRBV9":
                hexColor = "fde0dd";
                break;
            case "TRBV10":
                hexColor = "f7fcb9";
                break;
            case "TRBV11":
                hexColor = "d95f0e";
                break;
            case "TRBV12":
                hexColor = "b2df8a";
                break;
            case "TRBV13":
                hexColor = "e41a1c";
                break;
            case "TRBV14":
                hexColor = "66c2a5";
                break;
            case "TRBV15":
                hexColor = "bebada";
                break;
            case "TRBV16":
                hexColor = "377eb8";
                break;
            case "TRBV17":
                hexColor = "8dd3c7";
                break;
            case "TRBV18":
                hexColor = "af8dc3";
                break;
            case "TRBV19":
                hexColor = "ffff33";
                break;
            case "TRBV20":
                hexColor = "fb8072";
                break;
            case "TRBV21":
                hexColor = "b15928";
                break;
            case "TRBV22":
                hexColor = "fccde5";
                break;
            case "TRBV23":
                hexColor = "006837";
                break;
            case "TRBV24":
                hexColor = "6a51a3";
                break;
            case "TRBV25":
                hexColor = "dfc27d";
                break;
            case "TRBV26":
                hexColor = "e6f598";
                break;
            case "TRBV27":
                hexColor = "807dba";
                break;
            case "TRBV28":
                hexColor = "c2a5cf";
                break;
            case "TRBV29":
                hexColor = "006837";
                break;
            case "TRBV30":
                hexColor = "80cdc1";
                break;
            default:
                hexColor = "DCDCDC";
        }
        if (vGene.contains("-")) {
            Integer scale = Integer.parseInt(vGene.substring(vGene.indexOf("-") + 1));
            hexColor = vColorDarker(hexColor, scale);
        }
        return hexColor;
    }

    private String vColorDarker(String hex, int scale) {
        Integer intVal = Integer.parseInt(hex, 16);
        Integer R = intVal & 0xff0000;
        Integer G = intVal & 0x00ff00;
        Integer B = intVal & 0x0000ff;
        R = (int) Math.round(R * (1 - (0.01) * scale));
        B = (int) Math.round(B * (1 - (0.01) * scale));
        G = (int) Math.round(G * (1 - (0.01) * scale));
        intVal = R + G + B;
        String strVal = Integer.toHexString(Color.decode(intVal.toString()).getRGB() & 0xffffff);
        while (strVal.length() < 6) {
            strVal = "0" + strVal;
        }
        return strVal;
    }

    private Color decodeVColor(String hex) {
        Integer intVal  = Integer.parseInt(hex, 16);
        String strVal = intVal.toString();
        return Color.decode(strVal);
    }

    public String getHexVColor() {
        return "#" + hexColor;
    }

    public String getvGene() {
        return vGene;
    }



}
