package com.eliyetyang.piano.model

/**
 * @Description
 * @Author: Elite.Yang
 * @CreateDate: 20-6-15
 */
class Major {
    val allSound = arrayOf(
        "A2", "A#2", "B2",
        "C1", "C#1", "D1", "D#1", "E1", "F1", "F#1", "G1", "G#1", "A1", "A#1", "B1",
        "C", "C#", "D", "D#", "E", "F", "F#", "G", "G#", "A", "A#", "B",
        "c", "c#", "d", "d#", "e", "f", "f#", "g", "g#", "a", "a#", "b",
        "c1", "c#1", "d1", "d#1", "e1", "f1", "f#1", "g1", "g#1", "a1", "a#1", "b1",
        "c2", "c#2", "d2", "d#2", "e2", "f2", "f#2", "g2", "g#2", "a2", "a#2", "b2",
        "c3", "c#3", "d3", "d#3", "e3", "f3", "f#3", "g3", "g#3", "a3", "a#3", "b3",
        "c4", "c#4", "d4", "d#4", "e4", "f4", "f#4", "g4", "g#4", "a4", "a#4", "b4",
        "c5"
    )

    val majorStep = arrayOf(2, 2, 1, 2, 2, 2, 1)
    val harmonicMinorStep = arrayOf(2, 1, 2, 2, 1, 3, 1)
    val arpeggiosStepInScale = arrayOf(2, 2, 3)


    val normalStart = arrayOf("c", "d", "e", "f", "g", "a", "b")
}