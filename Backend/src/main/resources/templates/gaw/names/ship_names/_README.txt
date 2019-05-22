#IGNORE

This directory contains txt files which are used to define ship names. Each faction
will get a list of these names and whenever a ship is created, it pulls and removes
a name from the list and sets it on the new ship. When a ship is destroyed, the
name is put back into the list.

NOTE: 'i' is for integer, 'd' for double, and 's' for String. Values within ()
are the range the actual value should be within

Format:
type=s	- Name of the ship type
s	- Possible name for ship