package com.zzw.sketchpad.interfaces;
public interface IUndoRedoCommand {

	void undo();
    void redo();
    boolean canUndo();
    boolean canRedo();
    void onDeleteFromUndoStack();
    void onDeleteFromRedoStack();
}
