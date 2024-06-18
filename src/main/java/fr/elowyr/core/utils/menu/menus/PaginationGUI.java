package fr.elowyr.core.utils.menu.menus;

import fr.elowyr.core.utils.menu.BoardContainer;

public abstract class PaginationGUI extends VirtualGUI
{
    private int contentSize;
    private int page;
    private int maxItemsPerPage;

    public PaginationGUI(final int page, final int contentSize, final int maxItemsPerPage, final String name) {
        super(name, Size.SIX_LIGNE);
        this.contentSize = contentSize;
        this.page = page;
        this.maxItemsPerPage = maxItemsPerPage;
    }

    public int getMaxItemPerPage() {
        if (this.maxItemsPerPage != 0) {
            return this.maxItemsPerPage;
        }
        final Size size = this.getSize();
        return size.getSize() - 9;
    }

    public int getTotalPages() {
        final int maxItemPerPage = this.getMaxItemPerPage();
        final int listSize = this.contentSize;
        int size = listSize - 1;
        if (size <= maxItemPerPage) {
            size = 1;
        }
        else {
            size = size / maxItemPerPage + 1;
        }
        if (maxItemPerPage == 1) {
            if (listSize > 0) {
                size = listSize;
            }
            else {
                size = 1;
            }
        }
        return size;
    }

    public int[] getDatas() {
        final int maxItemPerPage = this.getMaxItemPerPage();
        final int[] data = { (this.page == 1) ? 0 : (maxItemPerPage * (this.page - 1)), (this.page == 1) ? maxItemPerPage : (maxItemPerPage * this.page) };
        if (data[1] > this.contentSize) {
            data[1] = this.contentSize;
        }
        return data;
    }

    public int getContentSize() {
        return this.contentSize;
    }

    public int getPage() {
        return this.page;
    }

    public int getMaxItemsPerPage() {
        return this.maxItemsPerPage;
    }

    public void setContentSize(final int contentSize) {
        this.contentSize = contentSize;
    }

    public void setPage(final int page) {
        this.page = page;
    }

    public BoardContainer getBoardContainer(int slotFrom, int lenght, int width) {
        return new BoardContainer(this, slotFrom, lenght, width);
    }

    public void setMaxItemsPerPage(final int maxItemsPerPage) {
        this.maxItemsPerPage = maxItemsPerPage;
    }
}
