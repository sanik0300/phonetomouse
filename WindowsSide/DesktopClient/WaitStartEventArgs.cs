using System;
using System.Collections.Generic;
using System.Linq;
using System.Text;
using System.Threading.Tasks;

namespace DesktopClient
{
    public class WaitStartEventArgs  : EventArgs
    {
        public bool ForReconnect { get; private set; }
        public WaitStartEventArgs(bool forReconnect)
        {
            this.ForReconnect = forReconnect;
        }
    }
}
