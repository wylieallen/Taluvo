package Taluvo.GUI.Clickables.Detectors;

import Taluvo.GUI.Clickables.Clickable;

import java.awt.*;
import java.util.Set;

public interface ClickDetector
{
    void reset();
    void initialize(Set<Clickable> clickables);
    void add(Clickable clickable);
    Clickable getClickable(Point point);
}
