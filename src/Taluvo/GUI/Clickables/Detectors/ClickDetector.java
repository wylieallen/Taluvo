package Taluvo.GUI.Clickables.Detectors;

import Taluvo.GUI.Clickables.Clickable;

import java.awt.*;
import java.util.Set;

public interface ClickDetector
{
    void reset();
    void initialize(Set<Clickable> clickables, Set<Clickable> staticClickables);
    void add(Clickable clickable);
    void addStatic(Clickable clickable);
    Clickable getClickable(Point point);
    Clickable getClickable(Point point, Point offset);
}
