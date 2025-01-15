using System.Runtime.InteropServices;

namespace MouseSimTestEnvironment
{
    internal class InputSimWrapper
    {
        public const int ScreenAnyAxisMax = 65535;

        [DllImport("MouseSimulationLib.dll", CharSet=CharSet.Unicode)]
        public static extern  void mouse_move(int x_dest, int y_dest);
        
        //--------------------------------
        [DllImport("MouseSimulationLib.dll", CharSet=CharSet.Unicode)]
        public static extern void left_key_down(int x, int y);

        [DllImport("MouseSimulationLib.dll", CharSet=CharSet.Unicode)]
        public static extern  void left_key_up(int x, int y);

        [DllImport("MouseSimulationLib.dll", CharSet=CharSet.Unicode)]
        public static extern  void left_click(int x, int y);
        //--------------------------------

        //--------------------------------
        [DllImport("MouseSimulationLib.dll", CharSet=CharSet.Unicode)]
        public static extern  void right_key_down(int x, int y);

        [DllImport("MouseSimulationLib.dll", CharSet=CharSet.Unicode)]
        public static extern  void right_key_up(int x, int y);

        [DllImport("MouseSimulationLib.dll", CharSet=CharSet.Unicode)]
        public static extern  void right_click(int x, int y);
        //--------------------------------

        [DllImport("MouseSimulationLib.dll", CharSet=CharSet.Unicode)]
        public static extern  void wheel_backward(int times);

        [DllImport("MouseSimulationLib.dll", CharSet=CharSet.Unicode)]
        public static extern  void wheel_forward(int times);
    }
}
