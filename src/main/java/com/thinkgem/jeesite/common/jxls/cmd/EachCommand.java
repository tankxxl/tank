package com.thinkgem.jeesite.common.jxls.cmd;

import org.jxls.area.Area;
import org.jxls.command.AbstractCommand;
import org.jxls.command.CellRefGenerator;
import org.jxls.common.CellRef;
import org.jxls.common.Context;
import org.jxls.common.Size;

/**
 *
 * http://blog.csdn.net/layman1024/article/details/71216343
 *
 * 扩展jxls each命令
 * 增加retainEmpty属性，当items为null或size为0时，也保留当前一行数据的格式
 * 循环增加下标变量"var_index"，如var="item"，获取下标方法：${item_index}
 */
public class EachCommand extends AbstractCommand {

    public enum Direction {RIGHT, DOWN}

    private String var;
    private String items;
    private String select;
    private Area area;
    private Direction direction = Direction.DOWN;
    private CellRefGenerator cellRefGenerator;
    private String multisheet;

    private String retainEmpty; // 当集合大小为0时，是否最少保留一行空行数据

    public EachCommand() {
    }

    /**
     *
     * @param var name of the key in the context to contain each collection items during iteration
     * @param items name of the collection bean in the context
     * @param direction defines processing by rows (DOWN - default) or columns (RIGHT)
     */
    public EachCommand(String var, String items, Direction direction) {
        this.var = var;
        this.items = items;
        this.direction = direction == null ? Direction.DOWN : direction;
    }

    public EachCommand(String var, String items, Area area) {
        this(var, items, area, Direction.DOWN);
    }

    public EachCommand(String var, String items, Area area, Direction direction) {
        this(var, items, direction);
        if (area != null) {
            this.area = area;
            addArea(this.area);
        }
    }

    /**
     *
     * @param var name of the key in the context to contain each collection items during iteration
     * @param items name of the collection bean in the context
     * @param area body area for this command
     * @param cellRefGenerator generates target cell ref for each collection item during iteration
     */
    public EachCommand(String var, String items, Area area, CellRefGenerator cellRefGenerator) {
        this(var, items, area, (Direction) null);
        this.cellRefGenerator = cellRefGenerator;
    }

    public Direction getDirection() {
        return direction;
    }

    public void setDirection(Direction direction) {
        this.direction = direction;
    }

    public void setDirection(String direction) {
        this.direction = Direction.valueOf(direction);
    }

    public CellRefGenerator getCellRefGenerator() {
        return cellRefGenerator;
    }

    public void setCellRefGenerator(CellRefGenerator cellRefGenerator) {
        this.cellRefGenerator = cellRefGenerator;
    }

    @Override
    public String getName() {
        return "each";
    }

    public String getVar() {
        return var;
    }

    public void setVar(String var) {
        this.var = var;
    }

    @Override
    public Size applyAt(CellRef cellRef, Context context) {
        return null;
    }
}
