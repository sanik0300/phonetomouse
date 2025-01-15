#include "pch.h"
#include "MouseSimFunctions.h"
#include <Windows.h>


void mouse_event_template(DWORD flag, int x, int y) 
{
	INPUT inp = { 0 };
	inp.type = INPUT_MOUSE;

	inp.mi.dx = x;
	inp.mi.dy = y;
	inp.mi.dwFlags = flag | MOUSEEVENTF_ABSOLUTE;

	inp.mi.mouseData = 0;
	inp.mi.time = 0;

	::SendInput(1, &inp, sizeof(INPUT));
}


MOUSESIM_API void mouse_move(int x_dest, int y_dest)
{
	mouse_event_template(MOUSEEVENTF_MOVE, x_dest, y_dest);
}

MOUSESIM_API void left_key_down(int x, int y)
{
	mouse_event_template(MOUSEEVENTF_LEFTDOWN, x, y);
}

MOUSESIM_API void left_key_up(int x, int y)
{
	mouse_event_template(MOUSEEVENTF_LEFTUP, x, y);
}
MOUSESIM_API void left_click(int x, int y)
{
	left_key_down(x, y);
	left_key_up(x, y);
}



MOUSESIM_API void right_key_down(int x, int y)
{
	mouse_event_template(MOUSEEVENTF_RIGHTDOWN, x, y);
}

MOUSESIM_API void right_key_up(int x, int y)
{
	mouse_event_template(MOUSEEVENTF_RIGHTUP, x, y);
}
MOUSESIM_API void right_click(int x, int y)
{
	right_key_down(x, y);
	right_key_up(x, y);
}





void wheel_event_template(int scroll)
{
	INPUT inp = { 0 };
	inp.type = INPUT_MOUSE;
	inp.mi.dwFlags = MOUSEEVENTF_WHEEL;

	POINT cursor_pos;
	GetCursorPos(&cursor_pos);
	inp.mi.dx = cursor_pos.x;
	inp.mi.dy = cursor_pos.y;

	inp.mi.mouseData = scroll * WHEEL_DELTA;
	inp.mi.time = 0;

	::SendInput(1, &inp, sizeof(INPUT));
}

MOUSESIM_API void wheel_backward(int times)
{
	wheel_event_template(times * -1);
}

MOUSESIM_API void wheel_forward(int times)
{
	wheel_event_template(times);
}
