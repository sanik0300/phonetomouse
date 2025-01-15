#pragma once
#ifdef MOUSESIMULATIONLIB_EXPORTS
#define MOUSESIM_API __declspec(dllexport)
#else
#define MOUSESIM_API __declspec(dllimport)
#endif

extern "C" MOUSESIM_API void  mouse_move(int x_dest, int y_dest);

extern "C" MOUSESIM_API void  left_key_down(int x, int y);
extern "C" MOUSESIM_API void  left_key_up(int x, int y);
extern "C" MOUSESIM_API void  left_click(int x, int y);


extern "C" MOUSESIM_API void  right_key_down(int x, int y);
extern "C" MOUSESIM_API void  right_key_up(int x, int y);
extern "C" MOUSESIM_API void  right_click(int x, int y);

extern "C" MOUSESIM_API void  wheel_backward(int times);
extern "C" MOUSESIM_API void  wheel_forward(int times);